import uuid
from datetime import datetime

from sqlalchemy import Column, DateTime, ForeignKey, String, Text
from sqlalchemy.orm import relationship

from app.core.database import Base
from app.core.types import GUID


class AdminLog(Base):
    __tablename__ = "admin_logs"

    id = Column(GUID(), primary_key=True, default=uuid.uuid4)
    admin_id = Column(GUID(), ForeignKey("users.id"), nullable=False)

    action = Column(String, nullable=False)  # ex: "suspension_compte", "suppression_offre"
    cible_type = Column(String, nullable=False)  # ex: "user", "offre"
    cible_id = Column(String, nullable=False)
    details = Column(Text, nullable=True)

    created_at = Column(DateTime, default=datetime.utcnow)

    admin = relationship("User")
