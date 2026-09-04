import uuid

from fastapi import APIRouter, Depends, HTTPException, UploadFile, status
from sqlalchemy.orm import Session

from app.core.config import settings
from app.core.database import get_db
from app.core.deps import get_current_user
from app.models.profiles import EtudiantProfile
from app.models.user import User
from app.schemas.profiles import EtudiantProfileOut
from app.services.cv_parser import ensure_storage_dir, parse_cv

router = APIRouter()

ALLOWED_CONTENT_TYPE = "application/pdf"
MAX_FILE_SIZE_MB = 5


@router.post("/etudiant/cv", response_model=EtudiantProfileOut)
async def upload_cv(
    file: UploadFile,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    if current_user.type_profil != "etudiant":
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Seul un etudiant peut deposer un CV.",
        )

    if file.content_type != ALLOWED_CONTENT_TYPE:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Seuls les fichiers PDF sont acceptes pour le moment.",
        )

    contents = await file.read()
    if len(contents) > MAX_FILE_SIZE_MB * 1024 * 1024:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Le fichier depasse la taille maximale de {MAX_FILE_SIZE_MB} Mo.",
        )

    storage_dir = ensure_storage_dir(settings.cv_storage_dir)
    file_path = storage_dir / f"{current_user.id}.pdf"

    with open(file_path, "wb") as f:
        f.write(contents)

    result = parse_cv(str(file_path))
    competences_detectees = result["competences_detectees"]

    profile = db.query(EtudiantProfile).filter(EtudiantProfile.user_id == current_user.id).first()

    if profile is None:
        profile = EtudiantProfile(id=uuid.uuid4(), user_id=current_user.id)
        db.add(profile)

    profile.cv_url = str(file_path)

    # Fusionne les competences detectees avec celles deja renseignees manuellement,
    # sans creer de doublons.
    competences_existantes = (
        [c.strip() for c in profile.competences.split(",")] if profile.competences else []
    )
    toutes_les_competences = list(dict.fromkeys(competences_existantes + competences_detectees))
    profile.competences = ", ".join(toutes_les_competences)

    db.commit()
    db.refresh(profile)

    return profile
