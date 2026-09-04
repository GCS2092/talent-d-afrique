import uuid
from datetime import datetime

from sqlalchemy import Boolean, Column, DateTime, Enum, String
from sqlalchemy.dialects.postgresql import UUID as PG_UUID
from sqlalchemy.types import CHAR, TypeDecorator

from app.core.database import Base


class GUID(TypeDecorator):
    """UUID stocke comme vrai UUID sur PostgreSQL, comme chaine ailleurs (portabilite)."""

    impl = CHAR
    cache_ok = True

    def load_dialect_impl(self, dialect):
        if dialect.name == "postgresql":
            return dialect.type_descriptor(PG_UUID())
        return dialect.type_descriptor(CHAR(36))

    def process_bind_param(self, value, dialect):
        if value is None:
            return value
        return str(value)

    def process_result_value(self, value, dialect):
        if value is None:
            return value
        return uuid.UUID(value) if not isinstance(value, uuid.UUID) else value


class User(Base):
    __tablename__ = "users"

    id = Column(GUID(), primary_key=True, default=uuid.uuid4)

    nom = Column(String, nullable=False)
    email = Column(String, unique=True, index=True, nullable=False)
    mot_de_passe_hash = Column(String, nullable=False)

    type_profil = Column(
        Enum("etudiant", "entreprise", "ecole", "freelance", name="type_profil"),
        nullable=False,
    )

    # RGPD (decisions section 6.1 du recap projet)
    consent_given_at = Column(DateTime, nullable=False, default=datetime.utcnow)
    consent_version = Column(String, nullable=False)

    created_at = Column(DateTime, default=datetime.utcnow)
    deleted_at = Column(DateTime, nullable=True)  # suppression logique (soft delete)

    is_active = Column(Boolean, default=True)
