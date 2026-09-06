import uuid

from sqlalchemy.orm import Session

from app.models.admin_log import AdminLog


def logger_action_admin(
    db: Session,
    *,
    admin_id: uuid.UUID,
    action: str,
    cible_type: str,
    cible_id: str,
    details: str | None = None,
) -> None:
    log = AdminLog(
        id=uuid.uuid4(),
        admin_id=admin_id,
        action=action,
        cible_type=cible_type,
        cible_id=cible_id,
        details=details,
    )
    db.add(log)
    db.commit()
