from app.core.matching_config import (
    POIDS_COMPETENCES_OBLIGATOIRES,
    POIDS_COMPETENCES_SOUHAITEES,
    POIDS_DISPONIBILITE,
    POIDS_EXPERIENCE,
    POIDS_SOFT_SKILLS,
    SEUIL_RECOMMANDATION,
)
from app.core.skills_dictionary import extract_skills_from_text


def _texte_vers_ensemble_competences(texte: str | None) -> set[str]:
    """Convertit un texte libre (ex: 'Python, React, SQL') en un ensemble de
    competences normalisees, en reutilisant le dictionnaire de synonymes."""
    if not texte:
        return set()
    # Le dictionnaire detecte deja les synonymes/variantes (ex: 'py' -> 'Python')
    detectees = set(extract_skills_from_text(texte))

    # Complete avec les termes tels quels si non couverts par le dictionnaire,
    # pour ne pas ignorer des competences valides mais absentes de la liste V1.
    termes_bruts = {t.strip() for t in texte.split(",") if t.strip()}
    return detectees | termes_bruts


def _score_couverture(requis: set[str], possede: set[str]) -> float:
    """Retourne le pourcentage (0-100) de l'ensemble 'requis' couvert par 'possede'.
    Renvoie 100 si rien n'est requis (rien a satisfaire)."""
    if not requis:
        return 100.0
    intersection = {r for r in requis if any(r.lower() == p.lower() for p in possede)}
    return (len(intersection) / len(requis)) * 100


def _score_disponibilite(candidat_dispo: str | None, offre_dispo: str | None) -> float:
    if not offre_dispo:
        return 100.0
    if not candidat_dispo:
        return 0.0
    return 100.0 if candidat_dispo.strip().lower() == offre_dispo.strip().lower() else 0.0


def _score_experience(candidat_annees: int | None, offre_niveau: str | None) -> float:
    """Heuristique simple pour la V1 : compare un nombre d'annees d'experience
    a un niveau demande exprime en texte libre. A affiner en V2 avec le NLP."""
    if not offre_niveau:
        return 100.0
    if candidat_annees is None:
        return 50.0  # information manquante : score neutre plutot que penalisant

    niveau = offre_niveau.lower()
    if "debutant" in niveau or "junior" in niveau:
        return 100.0 if candidat_annees <= 2 else 70.0
    if "senior" in niveau:
        return 100.0 if candidat_annees >= 5 else 40.0
    # cas "2-5 ans" ou similaire : on considere une correspondance large comme correcte
    return 80.0


def calculer_score_matching(
    *,
    candidat_competences: str | None,
    candidat_disponibilite: str | None,
    candidat_annees_experience: int | None,
    offre_competences_obligatoires: str | None,
    offre_competences_souhaitees: str | None,
    offre_soft_skills: str | None,
    offre_disponibilite: str | None,
    offre_niveau_experience: str | None,
) -> dict:
    """Calcule le score de compatibilite candidat/offre selon la ponderation V1.
    Retourne le score global (0-100) et le detail par critere, pour la
    transparence demandee dans le cahier des charges (section 3 : 'pourquoi ce score')."""

    competences_candidat = _texte_vers_ensemble_competences(candidat_competences)

    detail = {
        "competences_obligatoires": _score_couverture(
            _texte_vers_ensemble_competences(offre_competences_obligatoires),
            competences_candidat,
        ),
        "competences_souhaitees": _score_couverture(
            _texte_vers_ensemble_competences(offre_competences_souhaitees),
            competences_candidat,
        ),
        "experience": _score_experience(candidat_annees_experience, offre_niveau_experience),
        "disponibilite": _score_disponibilite(candidat_disponibilite, offre_disponibilite),
        "soft_skills": _score_couverture(
            _texte_vers_ensemble_competences(offre_soft_skills), competences_candidat
        ),
    }

    score_global = (
        detail["competences_obligatoires"] * POIDS_COMPETENCES_OBLIGATOIRES
        + detail["competences_souhaitees"] * POIDS_COMPETENCES_SOUHAITEES
        + detail["experience"] * POIDS_EXPERIENCE
        + detail["disponibilite"] * POIDS_DISPONIBILITE
        + detail["soft_skills"] * POIDS_SOFT_SKILLS
    ) / 100

    return {
        "score_global": round(score_global, 1),
        "detail": {k: round(v, 1) for k, v in detail.items()},
        "recommandee": score_global >= SEUIL_RECOMMANDATION,
    }
