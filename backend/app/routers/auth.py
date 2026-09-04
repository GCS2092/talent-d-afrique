from datetime import datetime

from fastapi import APIRouter, Depends, HTTPException, Request, status
from sqlalchemy.orm import Session

from app.core.database import get_db
from app.core.deps import get_current_user
from app.core.limiter import limiter
from app.core.security import (
    create_access_token,
    create_refresh_token,
    decode_token,
    hash_password,
    verify_password,
)
from app.models.user import User
from app.schemas.user import (
    AccessTokenOut,
    RefreshRequest,
    Token,
    UserCreate,
    UserLogin,
    UserOut,
)

router = APIRouter()


@router.post("/register", response_model=UserOut, status_code=status.HTTP_201_CREATED)
@limiter.limit("5/minute")
def register(request: Request, payload: UserCreate, db: Session = Depends(get_db)):
    if not payload.consentement:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Le consentement aux CGU est obligatoire.",
        )

    existing_user = db.query(User).filter(User.email == payload.email).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Un compte existe deja avec cet email.",
        )

    user = User(
        nom=payload.nom,
        email=payload.email,
        mot_de_passe_hash=hash_password(payload.mot_de_passe),
        type_profil=payload.type_profil,
        consent_given_at=datetime.utcnow(),
        consent_version=payload.consent_version,
    )
    db.add(user)
    db.commit()
    db.refresh(user)

    return user


@router.post("/login", response_model=Token)
@limiter.limit("10/minute")
def login(request: Request, payload: UserLogin, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == payload.email).first()

    if not user or not verify_password(payload.mot_de_passe, user.mot_de_passe_hash):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Email ou mot de passe incorrect.",
        )

    if not user.is_active or user.deleted_at is not None:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Ce compte est desactive.",
        )

    return Token(
        access_token=create_access_token(str(user.id)),
        refresh_token=create_refresh_token(str(user.id)),
    )


@router.post("/refresh", response_model=AccessTokenOut)
def refresh(payload: RefreshRequest, db: Session = Depends(get_db)):
    token_data = decode_token(payload.refresh_token)

    if token_data is None or token_data.get("type") != "refresh":
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Refresh token invalide ou expire.",
        )

    user = db.query(User).filter(User.id == token_data.get("sub")).first()
    if user is None or not user.is_active or user.deleted_at is not None:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Utilisateur introuvable ou compte desactive.",
        )

    return AccessTokenOut(access_token=create_access_token(str(user.id)))


@router.get("/me", response_model=UserOut)
def read_current_user(current_user: User = Depends(get_current_user)):
    return current_user
