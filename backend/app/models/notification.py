import uuid
from datetime import datetime

from sqlalchemy import Boolean, Column, DateTime, ForeignKey, String, Text
from sqlalchemy.orm import relationship

from app.core.database import Base
from app.core.types import GUID


class Notification(Base):
    __tablename__ = "notifications"

    id = Column(GUID(), primary_key=True, default=uuid.uuid4)
    user_id = Column(GUID(), ForeignKey("users.id"), nullable=False)

    titre = Column(String, nullable=False)
    message = Column(Text, nullable=True)
    type_evenement = Column(String, nullable=False)  # ex: "candidature_recue", "statut_change"

    lue = Column(Boolean, nullable=False, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)

    user = relationship("User")
