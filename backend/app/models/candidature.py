import uuid
from datetime import datetime

from sqlalchemy import Column, DateTime, Enum, ForeignKey, Text, UniqueConstraint
from sqlalchemy.orm import relationship

from app.core.database import Base
from app.core.types import GUID


class Candidature(Base):
    __tablename__ = "candidatures"
    __table_args__ = (
        UniqueConstraint("offre_id", "candidat_id", name="uq_candidature_offre_candidat"),
    )

    id = Column(GUID(), primary_key=True, default=uuid.uuid4)
    offre_id = Column(GUID(), ForeignKey("offres.id"), nullable=False)
    candidat_id = Column(GUID(), ForeignKey("users.id"), nullable=False)

    message = Column(Text, nullable=True)  # lettre de motivation courte, optionnelle

    statut = Column(
        Enum("recue", "en_cours", "entretien", "refusee", "acceptee", name="statut_candidature"),
        nullable=False,
        default="recue",
    )

    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

    offre = relationship("Offre")
    candidat = relationship("User")
