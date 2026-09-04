# TALENT D'AFRIQUE — Récapitulatif de l'installation

*Dernière mise à jour : ce qui a été fait jusqu'ici, avant d'attaquer le développement du socle (inscription/connexion).*

---

## 1. Structure du projet

```
talent-afrique/
├── backend/     → API (Python + FastAPI)
└── frontend/    → Interface web (React + Vite)
```

Deux dossiers séparés mais qui travailleront ensemble : le frontend affiche les pages et parle à l'API du backend, qui elle-même parle à la base de données.

---

## 2. Backend — ce qui est installé et pourquoi

Le backend est l'endroit où vit toute la logique du produit : comptes utilisateurs, offres, candidatures, moteur de matching, etc. Le frontend ne fait qu'afficher ce que le backend lui envoie.

| Dépendance | À quoi ça sert dans le projet |
|---|---|
| **fastapi** | Le framework qui fait tourner l'API — c'est lui qui reçoit les requêtes du frontend (« crée ce compte », « donne-moi les offres ») et renvoie les réponses. |
| **uvicorn** | Le serveur qui fait réellement tourner FastAPI (sans lui, FastAPI n'est que du code, pas un service accessible). |
| **python-multipart** | Nécessaire pour recevoir des fichiers envoyés depuis le frontend, typiquement le CV en PDF uploadé par un étudiant. |
| **sqlalchemy** | Fait le lien entre le code Python et la base de données : permet de manipuler des « comptes », « offres », « candidatures » comme des objets Python plutôt que d'écrire du SQL brut. |
| **psycopg2-binary** | Le connecteur technique qui permet à SQLAlchemy de parler concrètement à PostgreSQL. |
| **alembic** | Gère l'évolution de la base de données dans le temps (ex : le jour où on ajoute un champ « TJM » pour les freelances, alembic sait comment modifier une base déjà en production sans perdre les données existantes). |
| **pgvector** | Prépare le terrain pour le matching V2 (sémantique) : permet à PostgreSQL de stocker et comparer des « embeddings » (représentations mathématiques du sens d'un texte), sans avoir besoin d'une base de données séparée. |
| **python-jose** | Génère et vérifie les tokens de connexion (JWT) — c'est ce qui permet à un utilisateur de rester connecté sans retaper son mot de passe à chaque page. |
| **passlib** + **bcrypt** | Chiffre les mots de passe avant de les stocker en base. Un mot de passe n'est jamais enregistré en clair. |
| **python-dotenv** | Permet de garder les informations sensibles (mots de passe de la base de données, clés API) dans un fichier `.env` séparé du code, jamais partagé publiquement. |
| **pydantic** + **pydantic-settings** | Vérifie automatiquement que les données reçues sont valides (ex : qu'un email a bien la forme d'un email, qu'un champ obligatoire n'est pas vide) avant de les traiter. |
| **email-validator** | Complète pydantic spécifiquement pour valider le format des adresses email. |
| **pdfplumber** | Lit le contenu texte d'un CV au format PDF, pour en extraire les informations (compétences, expériences...). |
| **spacy** (+ modèle français `fr_core_news_md`) | Analyse le texte extrait du CV pour repérer les noms, compétences, dates, etc. — c'est le moteur qui permet de pré-remplir automatiquement le profil d'un étudiant à partir de son CV. |
| **pytesseract** | Fait de la reconnaissance de texte sur des images (OCR) — utile quand un CV est un PDF scanné (une image) plutôt qu'un vrai texte. |
| **pillow** | Bibliothèque de traitement d'image, nécessaire au fonctionnement de pytesseract. |
| **pdf2image** | Convertit les pages d'un PDF en images, étape nécessaire avant de faire de l'OCR dessus. |
| **resend** | Service d'envoi d'emails (confirmation d'inscription, notification de nouvelle candidature, etc.). |
| **redis** | Client pour un système de cache/sessions rapide — utile plus tard pour accélérer certaines opérations répétitives. |
| **pytest**, **httpx**, **pytest-asyncio** | Outils pour écrire des tests automatisés qui vérifient que le code fonctionne correctement avant chaque mise en production. |

### Outils système installés (en dehors de Python)

| Outil | À quoi ça sert |
|---|---|
| **Tesseract OCR** | Le moteur de reconnaissance de texte utilisé par `pytesseract` — sans lui installé sur la machine, la bibliothèque Python ne peut rien faire. |
| **Poppler** | Fournit les outils de conversion PDF → image utilisés par `pdf2image`. Installé manuellement via les binaires précompilés (pas via Chocolatey, qui ne fournissait que le code source à compiler). |

---

## 3. Frontend — ce qui est installé et pourquoi

Le frontend est ce que voient et utilisent réellement les étudiants, entreprises, écoles et freelances : les pages, les formulaires, les boutons.

| Dépendance | À quoi ça sert dans le projet |
|---|---|
| **React** (+ Vite) | Le framework qui construit l'interface. Vite est l'outil qui fait tourner le projet en développement et prépare la version finale pour la mise en ligne. Choisi notamment car réutilisable plus tard pour la version mobile (React Native). |
| **TypeScript** | Ajoute une vérification des types au JavaScript, ce qui limite les erreurs bêtes (ex : envoyer un texte là où un nombre est attendu) avant même de tester dans le navigateur. |
| **vite-plugin-pwa** | Rend l'application installable sur l'écran d'accueil d'un téléphone ou ordinateur, et permet un fonctionnement partiel hors connexion — un des points clés du cahier des charges (section 3). |
| **react-router-dom** | Gère la navigation entre les différentes pages (accueil, inscription, connexion, dashboard entreprise, etc.) sans recharger complètement le site à chaque clic. |
| **@tanstack/react-query** | Gère les échanges de données avec le backend (récupérer les offres, envoyer une candidature) de façon optimisée : mémorise les résultats, évite les requêtes inutiles, gère les états de chargement/erreur automatiquement. |
| **axios** | Bibliothèque qui envoie concrètement les requêtes HTTP vers l'API du backend. |
| **react-hook-form** | Gère les formulaires (inscription, création d'offre, etc.) de façon performante et avec moins de code répétitif. |
| **zod** | Définit des règles de validation (ex : « le mot de passe doit faire au moins 8 caractères ») et vérifie que les données du formulaire les respectent avant envoi. |
| **@hookform/resolvers** | Fait le lien entre `react-hook-form` et `zod`, pour que les deux fonctionnent ensemble. |
| **Tailwind CSS v4** | Système de style qui permet d'habiller les pages rapidement via des classes CSS prêtes à l'emploi, plutôt que d'écrire du CSS personnalisé pour chaque élément. Installé en v4 (méthode 2026 : plugin Vite direct, sans fichiers de config séparés comme avant). |
| **react-email** + **@react-email/components** | Permet de construire les templates d'emails (confirmation d'inscription, notification) avec des composants React plutôt qu'en HTML brut. *Note : ces paquets sont signalés comme n'étant plus activement maintenus par leurs auteurs — à surveiller, un remplacement pourra être envisagé si besoin plus tard.* |

---

## 4. Ce qui a été mis en place concrètement

- Le projet backend et frontend sont initialisés dans deux dossiers séparés.
- Toutes les dépendances ci-dessus sont installées et fonctionnelles.
- Tesseract et Poppler (OCR et traitement PDF) sont installés et vérifiés en ligne de commande.
- Une première page d'accueil fonctionne en local (`http://localhost:5173`), avec :
  - la charte graphique bleu / orange / blanc du cahier des charges,
  - le sélecteur des 4 types de profils (étudiant, entreprise, école, freelance),
  - une mise en page réalisée entièrement avec Tailwind CSS.

## 5. Ce qu'il reste à faire (prochaines étapes)

D'après l'ordre de réalisation du cahier des charges (section 7), la suite logique est :

1. **Le socle** : pages d'inscription et de connexion réelles, connectées au backend, avec choix du type de profil sauvegardé en base de données.
2. **Le routing frontend** : de vraies pages séparées (`/inscription`, `/connexion`, etc.) au lieu d'une page unique.
3. **La première route API** côté backend, avec connexion effective à la base de données PostgreSQL.

---

## 6. Décisions prises pour trancher les points ouverts (section 8 du cahier des charges)

Tous les points laissés en suspens dans le cahier des charges initial ont été tranchés avant d'attaquer le développement du socle (inscription/connexion), pour éviter de devoir revenir en arrière sur des choix structurants une fois le code écrit.

### 6.1 Sécurité et RGPD

| Sujet | Décision | Pourquoi |
|---|---|---|
| Mots de passe | Chiffrés avec bcrypt (déjà en place) | Standard fiable, impossible à retrouver en clair même en cas de fuite de la base |
| Connexion | Un jeton de connexion (JWT) valable 15-30 minutes, renouvelé automatiquement pendant 7 jours | Si un jeton est volé, il devient inutilisable rapidement |
| Consentement RGPD | Chaque compte enregistre la date et la version des CGU acceptées à l'inscription | Permet de prouver légalement que la personne a bien consenti, et à quel texte exactement |
| Suppression de compte | Le compte est d'abord masqué (« supprimé » aux yeux de l'utilisateur), puis réellement effacé de la base au bout de 30 jours | Évite qu'une suppression accidentelle ou un piratage de compte soit irréversible immédiatement, tout en respectant le droit à l'oubli |
| Export des données | Chaque utilisateur pourra demander une copie de toutes ses données dès la mise en place du socle | Obligation légale RGPD, plus simple à prévoir dès le début qu'à ajouter plus tard sur une base de données qui aura grossi |
| Protection contre le piratage de mots de passe | Limitation du nombre de tentatives de connexion par minute (via Redis, déjà installé) | Empêche un pirate de tester des milliers de mots de passe automatiquement |

### 6.2 Environnements et secrets

Trois environnements séparés et cloisonnés : développement local (sur la machine), pré-production (tests avant mise en ligne), production (le vrai site). Chacun a ses propres mots de passe et clés — jamais les mêmes d'un environnement à l'autre. Les vrais secrets de production ne seront jamais écrits dans le code, uniquement configurés directement chez l'hébergeur (Render, Vercel).

### 6.3 Qualité de code

| Outil | À quoi il sert |
|---|---|
| **Ruff** (à installer côté backend) | Relit automatiquement le code Python pour repérer les erreurs de style et les problèmes évidents avant qu'ils posent souci |
| **ESLint** (déjà installé côté frontend) | Fait la même chose côté React/TypeScript |
| **pre-commit** (à mettre en place) | Empêche d'enregistrer du code dans Git s'il ne respecte pas les règles ci-dessus |
| **pytest** (déjà installé) | Vérifie automatiquement que les fonctionnalités critiques (connexion, matching) fonctionnent toujours après chaque modification |

### 6.4 Suivi des erreurs en production

**Sentry** sera branché au moment de la mise en ligne : il alerte automatiquement en cas de bug réel chez un utilisateur, plutôt que de découvrir les problèmes par les plaintes.

### 6.5 Sauvegardes de la base de données

Supabase (prévu pour la production) effectue des sauvegardes automatiques sur son offre gratuite, mais avec une durée de conservation limitée — point à vérifier précisément au moment du déploiement, et à surveiller.

### 6.6 Pondération du moteur de matching V1

Pour éviter de rester bloqué sur une IA « floue », la formule suivante a été retenue comme point de départ (ajustable après les premiers retours utilisateurs) :

- Compétences obligatoires : **40 %**
- Compétences souhaitées : **20 %**
- Expérience : **20 %**
- Disponibilité : **10 %**
- Soft skills : **10 %**

Une offre avec un score inférieur à **40 %** ne sera pas mise en avant dans les recommandations du candidat (mais reste consultable s'il cherche activement).

Pour la gestion des synonymes de compétences (ex : « JS » = « JavaScript ») en attendant le moteur sémantique de la V2, on utilisera un simple dictionnaire de correspondances tenu à la main — suffisant pour démarrer, sans complexité inutile.

### 6.7 Mode hors ligne (PWA)

Périmètre retenu pour la V1 : les données déjà consultées (profil, dernières offres vues) restent lisibles sans connexion. En revanche, aucune action (postuler, envoyer un message) ne sera possible hors ligne dans cette première version — trop complexe à synchroniser correctement pour l'instant, cette fonctionnalité est repoussée à une version ultérieure.

### 6.8 Charte graphique

- Bleu principal : `#1D4ED8`
- Orange d'accent (boutons d'action) : `#F97316`
- Gris neutres pour les textes et fonds (palette « slate » de Tailwind)
- Police de caractères : **Inter**, lisible et largement utilisée sur les produits professionnels

### 6.9 Indicateurs de succès (KPIs) de la V1

- Nombre d'inscrits actifs par type de profil
- Taux de candidatures ayant reçu une réponse de l'entreprise
- Score moyen de matching des candidatures envoyées
- Temps moyen entre le dépôt d'une candidature et l'embauche

---

## 7. Fondations techniques mises en place (au-delà du cahier des charges initial)

Ces éléments ne figuraient pas explicitement dans le cahier des charges mais sont des bonnes pratiques indispensables pour un projet destiné à durer :

- **Git** : le projet est maintenant suivi par un système de versioning, avec un historique de chaque modification.
- **Structure de dossiers claire** : backend organisé en `routers/`, `models/`, `schemas/`, `services/`, `core/` ; frontend organisé en `pages/`, `components/`, `api/`, `hooks/`, `types/`.
- **Base de données locale via Docker** : plutôt que de dépendre de Supabase (en ligne) dès le développement, une base PostgreSQL identique tourne en local sur la machine, avec l'extension pgvector déjà activée (utile aussi bien en V1 qu'en V2). Le code écrit sera directement compatible avec Supabase au moment du déploiement.

## 8. Journal d'avancement (état au 04/09/2026)

### Ce qui est fait et fonctionnel

- **Projet Git** initialisé et poussé sur GitHub : https://github.com/GCS2092/talent-d-afrique
- **Structure de dossiers** complète (backend en `app/{core,routers,models,schemas,services}`, frontend en `src/{pages,components,api,hooks,types}`)
- **Backend FastAPI qui démarre et répond** :
  - `app/main.py` — point d'entrée, CORS configuré pour parler au frontend
  - `app/core/config.py` — lecture centralisée des variables d'environnement
  - `app/core/database.py` — connexion SQLAlchemy préparée (pas encore branchée à une vraie base)
  - `app/routers/health.py` — route de test `/api/health`
  - Vérifié fonctionnel sur `http://localhost:8000`, `/api/health` et `/docs`
- **Qualité de code en place** : Ruff configuré (`pyproject.toml`), pre-commit installé (`.pre-commit-config.yaml`)
- **Frontend React + Vite + Tailwind CSS v4 entièrement routé** (react-router-dom) :
  - `pages/Accueil.tsx` — sélection de profil, redirige vers l'inscription avec le profil choisi dans l'URL
  - `pages/Inscription.tsx` — formulaire complet avec validation Zod (nom, email, mot de passe, consentement RGPD obligatoire)
  - `pages/Connexion.tsx` — formulaire de connexion avec validation
  - `pages/CGU.tsx` et `pages/Confidentialite.tsx` — pages légales liées au consentement RGPD, avec système de version (`VERSION_CGU`) pour tracer quelle version un utilisateur a acceptée
  - `pages/DashboardEntreprise.tsx`, `DashboardEtudiant.tsx`, `DashboardEcole.tsx`, `DashboardFreelance.tsx` — coquilles vides par profil, à remplir une fois le backend connecté
  - `pages/NotFound.tsx` — page 404
  - `components/Header.tsx` — navigation partagée entre toutes les pages
  - `types/profile.ts` — typage partagé des 4 profils
  - Tous les formulaires valident correctement et affichent les erreurs, mais n'envoient pas encore de données réelles (`alert()` de test à la place d'un appel API, en attendant la connexion au backend)
- **Scripts pratiques** créés dans `scripts/` : `install.ps1` (tout installer), `dev.ps1` (tout lancer), `check.ps1` (vérifier le code)
- **Toutes les décisions structurantes tranchées** (sécurité, RGPD, pondération du matching, charte graphique, KPIs) — voir section 6 de ce document

### Ce qui N'EST PAS encore fait

- ❌ **Aucune base de données réelle connectée.** Tentative de création d'un projet Supabase en cours — un incident temporaire côté Supabase (dégradation de leur API Gateway) a bloqué une première tentative, à réessayer.
- ❌ Aucun modèle de données (`User`) n'est encore écrit.
- ❌ Aucune route d'inscription/connexion réelle côté backend.
- ❌ Les formulaires frontend ne sont pas encore branchés à l'API (actuellement juste un `console.log` + `alert()` de test).

### Prochaines étapes, dans l'ordre

1. Finaliser la création du projet Supabase (pgvector activé, Data API désactivée, RLS automatique activée)
2. Brancher le vrai `DATABASE_URL` dans `.env`
3. Écrire le modèle `User` en SQLAlchemy avec les champs RGPD (consentement, suppression différée)
4. Créer les routes d'inscription et de connexion (avec hash bcrypt + JWT)
5. Remplacer les `alert()` de test dans `Inscription.tsx` et `Connexion.tsx` par de vrais appels `axios` vers l'API
6. Tester le tout de bout en bout : inscription depuis le frontend → sauvegarde réelle en base → redirection vers le bon dashboard selon le profil

Cette liste correspond au tout début de l'étape 1 de l'ordre de réalisation du cahier des charges (section 7) : « Socle : inscription, connexion sécurisée, gestion de compte, choix du type de profil ».