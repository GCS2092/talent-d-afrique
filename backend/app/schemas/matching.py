from app.schemas.candidature import CandidatureOut
from app.schemas.offre import OffreOut


class MatchingDetail:
    pass  # place-holder si un typage plus strict est souhaite plus tard


class OffreAvecScore(OffreOut):
    score_global: float
    recommandee: bool
    detail_score: dict


class CandidatureAvecScore(CandidatureOut):
    score_global: float
    recommandee: bool
    detail_score: dict
