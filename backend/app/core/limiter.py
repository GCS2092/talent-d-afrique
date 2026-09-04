from slowapi import Limiter
from slowapi.util import get_remote_address

# Stockage en memoire pour le developpement local.
# A remplacer par Upstash Redis en production (voir section 5.1 du cahier des charges).
limiter = Limiter(key_func=get_remote_address)
