import uuid

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.deps import get_current_user
from app.models.profiles import (
    EcoleProfile,
    EntrepriseProfile,
    EtudiantProfile,
    FreelanceProfile,
)
from app.models.user import User
from app.schemas.profiles import (
    EcoleProfileIn,
    EcoleProfileOut,
    EntrepriseProfileIn,
    EntrepriseProfileOut,
    EtudiantProfileIn,
    EtudiantProfileOut,
    FreelanceProfileIn,
    FreelanceProfileOut,
)

router = APIRouter()

PROFILE_CONFIG = {
    "entreprise": (EntrepriseProfile, EntrepriseProfileOut),
    "etudiant": (EtudiantProfile, EtudiantProfileOut),
    "ecole": (EcoleProfile, EcoleProfileOut),
    "freelance": (FreelanceProfile, FreelanceProfileOut),
}


@router.get("/me")
def get_my_profile(
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    model, _ = PROFILE_CONFIG[current_user.type_profil]
    profile = db.query(model).filter(model.user_id == current_user.id).first()

    if profile is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail="Aucun profil complete pour cet utilisateur pour le moment.",
        )
    return profile


@router.put("/me")
def upsert_my_profile(
    payload: EntrepriseProfileIn | EtudiantProfileIn | EcoleProfileIn | FreelanceProfileIn,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    model, _ = PROFILE_CONFIG[current_user.type_profil]
    profile = db.query(model).filter(model.user_id == current_user.id).first()

    data = payload.model_dump(exclude_unset=True)

    if profile is None:
        profile = model(id=uuid.uuid4(), user_id=current_user.id, **data)
        db.add(profile)
    else:
        for key, value in data.items():
            setattr(profile, key, value)

    db.commit()
    db.refresh(profile)
    return profile


@router.get("/ecole/{ecole_id}/etudiants", response_model=list[EtudiantProfileOut])
def list_etudiants_de_ecole(
    ecole_id: uuid.UUID,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    if current_user.type_profil != "ecole":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Seule une ecole peut consulter cette liste.",
        )

    return db.query(EtudiantProfile).filter(EtudiantProfile.ecole_id == ecole_id).all()
