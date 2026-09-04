import uuid

from pydantic import BaseModel, Field


class EntrepriseProfileIn(BaseModel):
    logo_url: str | None = None
    description: str | None = None
    secteur: str | None = None
    taille: str | None = None
    culture: str | None = None
    localisation: str | None = None


class EntrepriseProfileOut(EntrepriseProfileIn):
    id: uuid.UUID
    user_id: uuid.UUID

    class Config:
        from_attributes = True


class EtudiantProfileIn(BaseModel):
    cv_url: str | None = None
    competences: str | None = None
    experiences: str | None = None
    formations: str | None = None
    disponibilite: str | None = None
    preferences: str | None = None
    ecole_id: uuid.UUID | None = None


class EtudiantProfileOut(EtudiantProfileIn):
    id: uuid.UUID
    user_id: uuid.UUID

    class Config:
        from_attributes = True


class EcoleProfileIn(BaseModel):
    nom_etablissement: str | None = None
    description: str | None = None
    localisation: str | None = None


class EcoleProfileOut(EcoleProfileIn):
    id: uuid.UUID
    user_id: uuid.UUID

    class Config:
        from_attributes = True


class FreelanceProfileIn(BaseModel):
    competences: str | None = None
    tjm: float | None = Field(default=None, ge=0)
    disponibilite: str | None = None
    portfolio_url: str | None = None
    annees_experience: int | None = Field(default=None, ge=0)


class FreelanceProfileOut(FreelanceProfileIn):
    id: uuid.UUID
    user_id: uuid.UUID

    class Config:
        from_attributes = True
