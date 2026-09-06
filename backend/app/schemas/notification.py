import uuid
from datetime import datetime

from pydantic import BaseModel


class NotificationOut(BaseModel):
    id: uuid.UUID
    titre: str
    message: str | None
    type_evenement: str
    lue: bool
    created_at: datetime

    class Config:
        from_attributes = True
