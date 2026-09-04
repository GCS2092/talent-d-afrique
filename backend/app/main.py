from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from slowapi import _rate_limit_exceeded_handler
from slowapi.errors import RateLimitExceeded

from app.core.config import settings
from app.core.database import Base, engine
from app.core.limiter import limiter
from app.models import user  # noqa: F401
from app.routers import auth, health, users

app = FastAPI(
    title="Talent d'Afrique API",
    description="API du moteur de matching et de la plateforme Talent d'Afrique",
    version="0.1.0",
)

app.state.limiter = limiter
app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[settings.frontend_url],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Cree les tables si elles n'existent pas encore (dev local ; Alembic prendra le relai pour la prod)
Base.metadata.create_all(bind=engine)

app.include_router(health.router, prefix="/api", tags=["health"])
app.include_router(auth.router, prefix="/api/auth", tags=["auth"])
app.include_router(users.router, prefix="/api/users", tags=["users"])


@app.get("/")
def root():
    return {"message": "Bienvenue sur l'API Talent d'Afrique"}
