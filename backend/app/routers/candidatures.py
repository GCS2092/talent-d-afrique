import uuid

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.deps import get_current_user
from app.models.candidature import Candidature
from app.models.offre import Offre
from app.models.profiles import EntrepriseProfile, EtudiantProfile, FreelanceProfile
from app.models.user import User
from app.schemas.candidature import CandidatureCreate, CandidatureOut
from app.schemas.matching import CandidatureAvecScore
from app.services.matching import calculer_score_matching

router = APIRouter()


def _get_offre_owned_by_current_entreprise(
    offre_id: uuid.UUID, current_user: User, db: Session
) -> Offre:
    if current_user.type_profil != "entreprise":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Seule une entreprise peut effectuer cette action.",
        )

    entreprise_profile = (
        db.query(EntrepriseProfile).filter(EntrepriseProfile.user_id == current_user.id).first()
    )
    if entreprise_profile is None:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Veuillez d'abord completer votre profil entreprise.",
        )

    offre = db.query(Offre).filter(Offre.id == offre_id).first()
    if offre is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Offre introuvable.")
    if offre.entreprise_id != entreprise_profile.id:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Cette offre ne vous appartient pas.",
        )
    return offre


@router.post("", response_model=CandidatureOut, status_code=status.HTTP_201_CREATED)
def create_candidature(
    payload: CandidatureCreate,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    if current_user.type_profil not in ("etudiant", "freelance"):
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Seuls les etudiants et freelances peuvent postuler a une offre.",
        )

    offre = db.query(Offre).filter(Offre.id == payload.offre_id).first()
    if offre is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Offre introuvable.")
    if offre.statut != "active":
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Cette offre n'accepte plus de candidatures.",
        )

    candidature = Candidature(
        id=uuid.uuid4(),
        offre_id=payload.offre_id,
        candidat_id=current_user.id,
        message=payload.message,
    )
    db.add(candidature)

    try:
        db.commit()
    except IntegrityError as exc:
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Vous avez deja postule a cette offre.",
        ) from exc

    db.refresh(candidature)
    return candidature


@router.get("/mine", response_model=list[CandidatureOut])
def list_my_candidatures(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Suivi des candidatures envoyees, cote candidat."""
    return (
        db.query(Candidature)
        .filter(Candidature.candidat_id == current_user.id)
        .order_by(Candidature.created_at.desc())
        .all()
    )


@router.get("/offre/{offre_id}", response_model=list[CandidatureOut])
def list_candidatures_for_offre(
    offre_id: uuid.UUID,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Dashboard entreprise : candidatures recues pour une offre precise."""
    _get_offre_owned_by_current_entreprise(offre_id, current_user, db)

    return (
        db.query(Candidature)
        .filter(Candidature.offre_id == offre_id)
        .order_by(Candidature.created_at.desc())
        .all()
    )


@router.get("/offre/{offre_id}/classees", response_model=list[CandidatureAvecScore])
def list_candidatures_classees(
    offre_id: uuid.UUID,
    score_min: float | None = None,
    disponibilite: str | None = None,
    statut: str | None = None,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    """Dashboard entreprise : candidats tries par score de compatibilite (cf. section 2.2).

    Filtres optionnels :
    - score_min : n'affiche que les candidatures dont le score global est >= a cette valeur
    - disponibilite : filtre exact sur la disponibilite declaree par le candidat
       - statut : filtre exact sur le statut (recue, en_cours, entretien, refusee, acceptee)
    """
    offre = _get_offre_owned_by_current_entreprise(offre_id, current_user, db)

    query = db.query(Candidature).filter(Candidature.offre_id == offre_id)
    if statut:
        query = query.filter(Candidature.statut == statut)
    candidatures = query.all()

    resultats = []
    for candidature in candidatures:
        candidat = candidature.candidat

        if candidat.type_profil == "etudiant":
            profile = (
                db.query(EtudiantProfile).filter(EtudiantProfile.user_id == candidat.id).first()
            )
            competences = profile.competences if profile else None
            candidat_disponibilite = profile.disponibilite if profile else None
            annees_experience = None
        else:
            profile = (
                db.query(FreelanceProfile).filter(FreelanceProfile.user_id == candidat.id).first()
            )
            competences = profile.competences if profile else None
            candidat_disponibilite = profile.disponibilite if profile else None
            annees_experience = profile.annees_experience if profile else None

        if disponibilite and (
            not candidat_disponibilite
            or candidat_disponibilite.strip().lower() != disponibilite.strip().lower()
        ):
            continue

        matching = calculer_score_matching(
            candidat_competences=competences,
            candidat_disponibilite=candidat_disponibilite,
            candidat_annees_experience=annees_experience,
            offre_competences_obligatoires=offre.competences_obligatoires,
            offre_competences_souhaitees=offre.competences_souhaitees,
            offre_soft_skills=offre.soft_skills,
            offre_disponibilite=offre.disponibilite,
            offre_niveau_experience=offre.niveau_experience,
        )

        if score_min is not None and matching["score_global"] < score_min:
            continue

        resultats.append(
            CandidatureAvecScore(
                **CandidatureOut.model_validate(candidature).model_dump(),
                score_global=matching["score_global"],
                recommandee=matching["recommandee"],
                detail_score=matching["detail"],
            )
        )

    resultats.sort(key=lambda c: c.score_global, reverse=True)
    return resultats
