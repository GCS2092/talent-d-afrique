import uuid

from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.deps import get_current_user
from app.models.notification import Notification
from app.models.user import User
from app.schemas.notification import NotificationOut

router = APIRouter()


@router.get("", response_model=list[NotificationOut])
def list_my_notifications(
    non_lues_seulement: bool = False,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    query = db.query(Notification).filter(Notification.user_id == current_user.id)
    if non_lues_seulement:
        query = query.filter(Notification.lue.is_(False))
    return query.order_by(Notification.created_at.desc()).all()


@router.patch("/{notification_id}/lue", response_model=NotificationOut)
def marquer_comme_lue(
    notification_id: uuid.UUID,
    current_user: User = Depends(get_current_user),
    db: Session = Depends(get_db),
):
    notification = (
        db.query(Notification)
        .filter(Notification.id == notification_id, Notification.user_id == current_user.id)
        .first()
    )
    if notification is None:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail="Notification introuvable."
        )

    notification.lue = True
    db.commit()
    db.refresh(notification)
    return notification
