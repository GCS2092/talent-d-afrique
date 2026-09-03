from pydantic_settings import BaseSettings


class Settings(BaseSettings):
    # Base de données
    database_url: str = "postgresql://user:password@localhost:5432/talent_afrique"

    # Sécurité
    secret_key: str = "changeme-en-production"
    algorithm: str = "HS256"
    access_token_expire_minutes: int = 30

    # Email
    resend_api_key: str = ""

    # Frontend (pour CORS)
    frontend_url: str = "http://localhost:5173"

    class Config:
        env_file = ".env"


settings = Settings()
