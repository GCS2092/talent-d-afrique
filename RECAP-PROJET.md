# TALENT D'AFRIQUE — Récapitulatif spécial BACKEND

*Ce document liste chaque fichier du backend, à quoi il sert, et ce qu'il reste à faire. Objectif : te repérer immédiatement à la reprise, sans avoir à relire tout le code.*

---

## 1. Où en est le backend

Le socle d'authentification complet est **terminé, testé et validé** : inscription, connexion, gestion de compte (modification, export RGPD, suppression), protection anti-bruteforce, migrations gérées par Alembic.

**Huit étapes du cahier des charges sont maintenant complètes et testées :**
1. ✅ Socle (inscription, connexion, gestion de compte)
2. ✅ Profils complets par type d'utilisateur + parsing automatique de CV
3. ✅ CRUD des offres d'emploi avec contrôle de propriété
4. ✅ Dépôt de candidature et suivi de statut, des deux côtés (candidat et entreprise)
5. ✅ Moteur de matching V1 (scoring pondéré), avec transparence du détail par critère
6. ✅ Dashboard entreprise avec filtres (score minimum, disponibilité, statut) sur les candidats classés
7. ✅ Espace école : rattachement d'étudiants, suggestions d'offres par étudiant, statistiques d'employabilité
8. ✅ Espace freelance : type de contrat "mission", budget TJM sur les offres, matching adapté (comparaison TJM candidat/budget plutôt qu'expérience classique)

Prochaine étape : l'étape 9 du cahier des charges — les notifications (in-app et email) sur les événements clés (nouvelle offre matchée, candidature reçue, changement de statut).

---

## 2. Chaque fichier, un par un

### `app/main.py`
**Rôle** : le chef d'orchestre. C'est le fichier qui démarre l'application, connecte tous les morceaux entre eux (CORS, routes, base de données) et qu'on lance avec `uvicorn`.
**Pourquoi il est important** : c'est le seul fichier qu'on exécute directement. Si une route existe dans un autre fichier mais n'est pas "branchée" ici via `include_router`, elle n'existera pas pour de vrai.
**État** : à jour, inclut désormais 3 groupes de routes (health, auth, users) et le système anti-spam (rate limiting).

### `app/core/config.py`
**Rôle** : le tableau de bord des réglages. Centralise toutes les valeurs qui changent selon l'environnement (mot de passe de la base, durée de vie des sessions, adresse du frontend...).
**Pourquoi il est important** : évite d'avoir des valeurs codées en dur éparpillées dans 10 fichiers différents. Un seul endroit à modifier.
**État** : à jour, inclut maintenant la durée du "refresh token" (voir plus bas).

### `app/core/database.py`
**Rôle** : établit la connexion technique à la base de données PostgreSQL.
**Pourquoi il est important** : sans lui, aucun fichier ne peut lire ou écrire dans la base.
**État** : fonctionnel, connecté à la base locale créée via pgAdmin4.

### `app/core/security.py`
**Rôle** : la boîte à outils de sécurité. Contient les fonctions qui chiffrent les mots de passe et qui créent/vérifient les "tickets d'entrée" numériques (tokens) qui prouvent qu'un utilisateur est bien connecté.
**Pourquoi il est important** : c'est le cœur de la sécurité de toute la plateforme. Une faille ici mettrait en danger tous les comptes.
**État** : fonctionnel. Utilise la librairie `bcrypt` directement (pas `passlib`, abandonnée car buggée avec les versions récentes — voir section 4). Gère maintenant deux types de tickets : un ticket court ("access token", 30 minutes) et un ticket long ("refresh token", 7 jours) qui permet de renouveler le premier sans redemander le mot de passe.

### `app/core/deps.py`
**Rôle** : le videur à l'entrée des routes protégées. Vérifie, à chaque requête sur une route sensible, que la personne a bien un ticket d'entrée valide, et va chercher qui elle est dans la base.
**Pourquoi il est important** : sans ce fichier, il faudrait réécrire la même vérification de sécurité dans chaque route une par une, avec un risque d'oubli.
**État** : nouveau ce tour-ci, fonctionnel.

### `app/core/limiter.py`
**Rôle** : le videur qui compte les tentatives. Limite le nombre de fois qu'une même personne peut essayer de se connecter ou de créer un compte en peu de temps.
**Pourquoi il est important** : bloque les attaques automatisées qui testent des milliers de mots de passe à la suite.
**État** : nouveau ce tour-ci. Fonctionne en mémoire pour l'instant (suffisant en développement) ; devra migrer vers Upstash Redis avant la mise en production (prévu dans le cahier des charges, section 5.1) pour fonctionner correctement si plusieurs serveurs tournent en même temps.

### `app/models/user.py`
**Rôle** : le plan de construction de la table "utilisateurs" dans la base de données. Décrit quelles informations sont stockées pour chaque compte.
**Pourquoi il est important** : c'est la source de vérité de ce qu'est "un utilisateur" pour toute l'application.
**État** : fonctionnel, inclut les champs RGPD décidés en amont (date de consentement, version des CGU acceptée, date de suppression pour la suppression différée).

### `app/schemas/user.py`
**Rôle** : le videur à l'entrée et à la sortie de l'API. Vérifie que les données envoyées par le frontend ont la bonne forme (un email est bien un email, un mot de passe fait au moins 8 caractères...) et définit précisément ce que l'API renvoie (jamais le mot de passe, par exemple).
**Pourquoi il est important** : évite d'enregistrer des données invalides en base, et évite de fuiter accidentellement des informations sensibles dans les réponses.
**État** : étendu ce tour-ci avec les formats pour la modification de profil et les tickets de renouvellement.

### `app/routers/health.py`
**Rôle** : une route toute simple qui répond "je fonctionne" — sert à vérifier rapidement que le serveur est en vie.
**État** : inchangé depuis le début, toujours utile pour les tests rapides.

### `app/routers/auth.py`
**Rôle** : regroupe toutes les routes liées à l'identité : créer un compte, se connecter, renouveler son ticket d'accès, consulter qui on est.
**Pourquoi il est important** : c'est la porte d'entrée de toute la plateforme.
**État** : complet pour le socle. Contient désormais 4 routes : inscription, connexion, renouvellement de session, et consultation de son propre profil.

### `app/routers/users.py`
**Rôle** : regroupe les routes de gestion de son propre compte une fois connecté : modifier ses informations, exporter ses données, supprimer son compte.
**Pourquoi il est important** : c'est la mise en œuvre concrète des droits RGPD décidés en amont (droit de rectification, d'export, à l'oubli).
**État** : nouveau ce tour-ci, fonctionnel. La suppression est "logique" (le compte est désactivé, pas effacé immédiatement) — la purge définitive après 30 jours n'est pas encore automatisée (voir section 3).

### `requirements.txt`
**Rôle** : la liste de courses de toutes les librairies Python nécessaires au projet.
**Pourquoi il est important** : permet à n'importe qui (ou à toi sur une autre machine) de tout réinstaller à l'identique avec une seule commande.
**État** : à jour, inclut désormais `slowapi` (rate limiting) ; ne contient plus `passlib` (retirée).

### `pyproject.toml`
**Rôle** : la feuille de règles de qualité de code (Ruff), qui repère automatiquement les erreurs de style ou les oublis avant qu'ils posent problème.
**État** : à jour. Une règle (`B008`) a dû être désactivée volontairement, car elle signalait à tort une pratique qui est en réalité normale et recommandée avec FastAPI.

### `app/core/types.py`
**Rôle** : contient un type de donnée technique réutilisable (`GUID`) qui permet d'utiliser des identifiants uniques (UUID) de façon compatible avec PostgreSQL comme avec d'autres bases de données.
**Pourquoi il est important** : évite de dupliquer ce code technique dans chaque fichier de modèle qui en a besoin — extrait ici une fois que 5 modèles différents (utilisateur + 4 profils) s'en servent.
**État** : nouveau ce tour-ci, fonctionnel.

### `app/models/profiles.py`
**Rôle** : le plan de construction de 4 tables distinctes — une par type de profil (`EntrepriseProfile`, `EtudiantProfile`, `EcoleProfile`, `FreelanceProfile`) — chacune avec les informations propres à ce type d'utilisateur (secteur et logo pour une entreprise, compétences et CV pour un étudiant, TJM pour un freelance...).
**Pourquoi il est important** : c'est la mise en œuvre concrète de la section 2 du cahier des charges (besoins fonctionnels par persona). Un étudiant est aussi lié à une école via `ecole_id`, ce qui permettra plus tard à une école de retrouver "ses" étudiants.
**État** : nouveau ce tour-ci, fonctionnel et testé pour le profil étudiant.

### `app/schemas/profiles.py`
**Rôle** : définit les formats de données valides pour chacun des 4 profils, à l'entrée comme à la sortie de l'API.
**État** : nouveau ce tour-ci, fonctionnel.

### `app/routers/profiles.py`
**Rôle** : les routes qui permettent à un utilisateur connecté de consulter et modifier son propre profil, quel que soit son type — une seule route s'adapte automatiquement selon si c'est un étudiant, une entreprise, une école ou un freelance. Contient aussi une route dédiée pour qu'une école consulte la liste de ses étudiants.
**Pourquoi il est important** : simplifie le travail du frontend, qui n'a pas besoin de connaître 4 routes différentes selon le profil — une seule suffit.
**État** : nouveau ce tour-ci, fonctionnel et testé (création, consultation, gestion des cas où le profil n'existe pas encore).

### `app/core/skills_dictionary.py`
**Rôle** : une liste manuelle de compétences connues (Python, React, SQL...) et de leurs différentes façons d'être écrites (synonymes, abréviations). Sert à repérer ces compétences dans un texte brut.
**Pourquoi il est important** : c'est la mise en œuvre concrète de la décision prise en amont (section 6.6 du récap projet) pour gérer les synonymes en V1, avant l'arrivée du moteur sémantique plus avancé prévu en V2.
**État** : nouveau ce tour-ci, fonctionnel. Volontairement limité à une quinzaine de compétences pour démarrer — à enrichir progressivement.

### `app/services/cv_parser.py`
**Rôle** : le cœur du parsing de CV. Ouvre un fichier PDF, en extrait le texte ; si le PDF est en réalité une image scannée (peu de texte détecté), bascule automatiquement sur la reconnaissance optique de caractères (OCR) pour quand même récupérer le contenu. Une fois le texte obtenu, détecte les compétences qu'il contient.
**Pourquoi il est important** : c'est la fonctionnalité "pré-remplissage automatique du profil" prévue dans le cahier des charges (section 2.3), pour éviter à un étudiant de tout ressaisir à la main.
**État** : nouveau ce tour-ci, fonctionnel et testé avec succès.

### `app/routers/cv.py`
**Rôle** : la route qui reçoit un fichier PDF envoyé par un étudiant, vérifie qu'il s'agit bien d'un étudiant et d'un vrai PDF (pas trop volumineux), sauvegarde le fichier, l'analyse, puis met à jour le profil avec les compétences trouvées — sans effacer celles déjà renseignées à la main.
**Pourquoi il est important** : relie le service d'analyse (`cv_parser.py`) au reste de l'application de façon sécurisée (seul le propriétaire du profil peut déposer son propre CV).
**État** : nouveau ce tour-ci, fonctionnel et testé de bout en bout en ligne de commande.

### `app/models/offre.py`
**Rôle** : le plan de construction de la table "offres". Décrit tout ce qu'une offre d'emploi contient : titre, description, type de contrat (stage/CDD/CDI), compétences obligatoires et souhaitées séparément, soft skills, niveau d'expérience, disponibilité, localisation, rémunération, et un statut (active/expirée/archivée).
**Pourquoi il est important** : structure directement héritée de la section 2.2 du cahier des charges. La séparation entre compétences "obligatoires" et "souhaitées" est essentielle : c'est elle qui permettra au futur moteur de matching (étape 5) de pondérer différemment ces deux catégories.
**État** : nouveau ce tour-ci, fonctionnel et testé.

### `app/schemas/offre.py`
**Rôle** : définit les formats valides pour créer, modifier et afficher une offre.
**État** : nouveau ce tour-ci, fonctionnel.

### `app/routers/offres.py`
**Rôle** : toutes les routes liées aux offres — création, liste publique (visible par tous, filtrée sur les offres actives par défaut), liste privée ("mes offres" pour une entreprise, incluant les archivées), consultation d'une offre précise, modification, suppression.
**Pourquoi il est important** : contient une vérification systématique qu'une entreprise ne peut modifier ou supprimer que **ses propres offres** — un point de sécurité qu'on avait identifié comme manquant dans une précédente version du récap.
**État** : nouveau ce tour-ci, fonctionnel et testé de bout en bout (création, listes, modification de statut, filtrage).

### `app/models/candidature.py`
**Rôle** : le plan de construction de la table "candidatures" — relie un candidat (étudiant ou freelance) à une offre, avec un message optionnel et un statut (reçue, en cours, entretien, refusée, acceptée).
**Pourquoi il est important** : contient une contrainte technique qui empêche, au niveau même de la base de données, qu'un même candidat postule deux fois à la même offre — plus robuste qu'une simple vérification dans le code.
**État** : nouveau ce tour-ci, fonctionnel et testé.

### `app/schemas/candidature.py`
**Rôle** : définit les formats valides pour déposer une candidature et changer son statut.
**État** : nouveau ce tour-ci, fonctionnel.

### `app/routers/candidatures.py`
**Rôle** : les routes de candidature — déposer une candidature (réservé aux étudiants et freelances, uniquement sur une offre active), suivre ses propres candidatures, consulter les candidatures reçues pour une offre (réservé à l'entreprise propriétaire), classer les candidatures par score de compatibilité, changer le statut d'une candidature.
**Pourquoi il est important** : c'est la mise en œuvre de la mise en relation candidat/entreprise, avec des contrôles de sécurité stricts (une entreprise ne peut voir ou modifier que les candidatures liées à ses propres offres).
**État** : fonctionnel et testé de bout en bout (dépôt, rejet des doublons, suivi de statut visible des deux côtés, classement par score).

### `app/core/matching_config.py`
**Rôle** : un seul petit fichier qui contient tous les réglages du moteur de matching — le poids de chaque critère (compétences obligatoires, souhaitées, expérience, disponibilité, soft skills) et le seuil en dessous duquel une offre n'est pas mise en avant.
**Pourquoi il est important** : centralise la décision prise en amont (section 6.6 du récap projet) dans un seul endroit modifiable, sans avoir à toucher au code de calcul si les pourcentages doivent changer suite aux retours utilisateurs.
**État** : nouveau ce tour-ci, fonctionnel.

### `app/services/matching.py`
**Rôle** : le cœur du moteur de matching V1. Compare les compétences, la disponibilité et l'expérience d'un candidat à ce que demande une offre, calcule un score global sur 100 ainsi qu'un détail par critère (pour la transparence demandée en section 3 du cahier des charges — "pourquoi ce score"), et détermine si l'offre doit être mise en avant ou non.
**Pourquoi il est important** : c'est la fonctionnalité présentée dans le cahier des charges comme "la colonne vertébrale du produit" (section 4). Réutilise le dictionnaire de synonymes de compétences déjà existant.
**État** : nouveau ce tour-ci, fonctionnel et testé avec des scores cohérents (validé avec un cas à 100% de correspondance et un cas à 0%).

### `app/schemas/matching.py`
**Rôle** : étend les schémas d'offre et de candidature existants pour y ajouter les informations de score, sans dupliquer tous leurs champs.
**État** : nouveau ce tour-ci, fonctionnel.

### `scripts/test-matching.ps1`
**Rôle** : un script qui automatise entièrement le test du moteur de matching — connexion des deux comptes de test, création d'une offre propre, dépôt d'une candidature, affichage des scores des deux points de vue (candidat et entreprise).
**Pourquoi il est important** : évite de redéfinir manuellement une offre de test à chaque fois dans Swagger (risque d'oublier de remplir les champs, comme cela a été le cas une fois avec une offre restée à ses valeurs d'exemple "string"). Rejouable à l'identique après chaque modification du moteur de matching.
**État** : nouveau ce tour-ci, fonctionnel et validé.

### `scripts/test-filtres.ps1`
**Rôle** : un script qui teste automatiquement les 3 filtres du dashboard entreprise (score minimum, disponibilité, statut), avec un résultat "OK" ou "INATTENDU" affiché pour chaque cas — pas besoin de comparer les résultats à la main.
**Pourquoi il est important** : les filtres combinent plusieurs cas limites (aucun résultat attendu, un résultat attendu) qu'il serait fastidieux et sujet à erreur de vérifier manuellement dans Swagger à chaque modification du code.
**État** : fonctionnel et validé (6 tests, tous "OK").

### `app/routers/profiles.py` (routes ajoutées à ce tour-ci)
**Rôle** : trois nouvelles routes sont venues compléter ce fichier — permettre à un étudiant de se rattacher à une école existante, générer pour une école des suggestions d'offres pour chacun de ses étudiants (top 5 par étudiant, en réutilisant directement le moteur de matching), et calculer des statistiques simples d'employabilité de la promotion (nombre de candidatures envoyées/acceptées, taux de placement).
**Pourquoi c'est important** : c'est la mise en œuvre concrète de la section 2.4 du cahier des charges. Les suggestions et statistiques ne créent aucune nouvelle donnée — elles recalculent tout à la volée à partir des tables déjà existantes (profils, offres, candidatures), donc aucune migration n'a été nécessaire pour cette étape.
**État** : fonctionnel et testé de bout en bout (rattachement, liste, suggestions cohérentes avec le moteur de matching, statistiques exactes).

### `scripts/test-ecole.ps1`
**Rôle** : automatise tout le scénario de l'espace école — création du compte, connexion, création du profil, rattachement d'un étudiant existant, consultation de la liste, des suggestions et des statistiques.
**État** : fonctionnel et validé.

### Étape 8 — Espace freelance et matching adapté

**Modification du modèle `Offre`** : le type de contrat accepte désormais une quatrième valeur, `mission`, en plus de stage/CDD/CDI. Un nouveau champ `budget_tjm` (taux journalier maximum que l'entreprise est prête à payer) a été ajouté.
**Pourquoi c'est important** : c'est la traduction concrète de la demande du cahier des charges (section 2.5) d'avoir des "critères adaptés, différents du CDI/CDD" pour les freelances — le concept d'expérience en années n'a pas vraiment de sens pour une mission ponctuelle, le budget si.

**Modification du moteur de matching (`app/services/matching.py`)** : une nouvelle fonction `_score_tjm` compare le TJM déclaré par le freelance au budget de la mission (score maximal si le TJM rentre dans le budget, pénalité proportionnelle au-delà). Le critère "expérience" de la pondération générale (toujours 20% du score, décision section 6.6 du récap) utilise maintenant soit `_score_tjm` (si l'offre est une mission), soit l'ancien `_score_experience` (pour stage/CDD/CDI) — sans avoir eu besoin de créer une pondération séparée.
**État** : fonctionnel et testé — un freelance à 150 de TJM obtient un score d'expérience de 100 sur une mission à 200 de budget, et de 12.5 sur une mission à 80 de budget (dépassement pénalisé proportionnellement).

**Point de vigilance rencontré et résolu** : ajouter une valeur à un `Enum` PostgreSQL déjà existant (`type_contrat`) n'est **jamais détecté automatiquement** par `alembic revision --autogenerate` — seul l'ajout de colonne (`budget_tjm`) a été détecté. La ligne `op.execute("ALTER TYPE type_contrat ADD VALUE IF NOT EXISTS 'mission'")` a dû être ajoutée manuellement dans le fichier de migration. À refaire de la même façon pour toute future modification d'un enum existant (ajouter une valeur à `statut_offre` ou `statut_candidature`, par exemple).

### `scripts/test-freelance.ps1`
**Rôle** : automatise le test complet de l'espace freelance — création du compte, profil avec TJM, création de deux missions (une dans le budget, une hors budget), vérification que le score de matching reflète bien cette différence.
**État** : nouveau ce tour-ci, fonctionnel et validé.
**Rôle** : la configuration du système de migrations. Décrit comment Alembic doit se connecter à la base et où trouver la description des tables (`Base.metadata`).
**Pourquoi il est important** : c'est ce qui permet de faire évoluer la structure de la base de données (ajouter une colonne, une table) de façon tracée et réversible, plutôt que de modifier la base à la main ou de tout recréer à chaque redémarrage.
**État** : nouveau ce tour-ci, fonctionnel. Configuré pour lire `DATABASE_URL` depuis le `.env` (pas de mot de passe en dur dans un fichier versionné).

### `alembic/versions/xxxx_creation_table_users.py`
**Rôle** : la toute première "photo" de la structure de la base — décrit comment créer la table `users` depuis une base vide.
**Pourquoi il est important** : chaque futur changement de structure (nouvelle table, nouvelle colonne) donnera lieu à un nouveau fichier de ce type, formant un historique complet et applicable pas à pas.
**État** : générée et appliquée avec succès.

---

## 3. Ce qui reste à faire côté backend (dans l'ordre de priorité)

### Avant la mise en production (pas urgent maintenant, mais à ne pas oublier)
- **Le "refresh token" ne peut pas être invalidé avant son expiration naturelle.** Actuellement, si un utilisateur perd son appareil, son ticket de renouvellement reste valable jusqu'à 7 jours. Une vraie solution nécessiterait de garder une trace des tickets valides quelque part (Redis), pas urgent pour du développement mais important avant l'ouverture au public.
- **La création automatique des tables au démarrage a été retirée** — c'est maintenant Alembic qui gère la structure de la base, une méthode fiable pour la production. ✅ Réglé.
- **La suppression de compte n'efface pas réellement les données après 30 jours** — il faudra une tâche automatique qui tourne régulièrement pour faire cette purge, ce qui n'existe pas encore.
- **Le système anti-spam (rate limiting) ne fonctionne correctement que sur un seul serveur.** S'il y a plusieurs serveurs en production, il faudra le brancher sur Upstash Redis pour que la limite soit partagée entre eux.

### Pour avancer dans le cahier des charges (étape 9 et suivantes)
- **L'étape 8 est maintenant complète** (espace freelance et matching adapté par TJM, testé de bout en bout). ✅
- **Aucune notification (in-app ou email) n'existe encore** — c'est le prochain chantier (étape 9). Resend est installé depuis le début du projet mais aucune route ne l'utilise encore. Il faudra décider quels événements déclenchent une notification (nouvelle candidature reçue, changement de statut, nouvelle offre correspondant au profil d'un candidat) et où stocker les notifications in-app (nouvelle table à créer).
- Le score d'expérience/TJM reste une heuristique simple. Le cahier des charges prévoit d'affiner cela avec du NLP en V2 — pas un défaut à corriger maintenant, une limite connue et assumée de la V1.
- Le stockage des CV est actuellement **local** (dossier `storage/` sur la machine de développement, exclu de Git). À migrer vers Supabase Storage avant la mise en production (prévu dans le cahier des charges, section 5.1).
- Les données de test se sont accumulées en base au fil des scripts (plusieurs offres nommées "Mission React...", "Developpeur Full-Stack..."). Sans danger, mais un nettoyage (`DELETE FROM offres WHERE ...`) peut être fait à tout moment si la base de test devient difficile à lire dans pgAdmin4.

---


## 4. Petits pièges rencontrés, pour ne pas retomber dedans

- **`passlib` est abandonnée par ses créateurs et casse avec les versions récentes de `bcrypt`.** Le hash des mots de passe passe maintenant directement par la librairie `bcrypt`, sans intermédiaire. Ne pas réinstaller `passlib`.
- **La règle Ruff `B008`** (qui critique l'usage de `Depends(...)` dans les arguments par défaut) est un faux positif avec FastAPI — c'est le fonctionnement normal du framework. Elle a été désactivée volontairement dans `pyproject.toml`, ce n'est pas un oubli.
- **pgvector n'est pas installé** sur la base PostgreSQL locale (nécessiterait une compilation manuelle sous Windows). Sans importance pour l'instant : cette extension ne sert qu'au matching V2, qui est très loin dans l'ordre de réalisation du projet.
- **Alembic doit connaître TOUS les modèles pour détecter les changements.** Le fichier `alembic/env.py` importe chaque fichier de modèle (`from app.models import offre, profiles, user`) uniquement pour que Python les charge en mémoire — sans cet import, Alembic ne "voit" pas les nouvelles tables et génère des migrations vides (juste `pass`). À chaque nouveau fichier de modèle créé, il faut penser à l'ajouter à cet import — piège rencontré deux fois de suite sur ce projet.
- **Les migrations auto-générées par Alembic oublient systématiquement l'import du type `GUID`.** Alembic écrit son chemin complet (`app.core.types.GUID()`) dans le fichier de migration mais n'ajoute jamais l'import correspondant — il faut l'ajouter à la main (`from app.core.types import GUID`) et retirer le préfixe `app.core.types.` de chaque usage, à chaque nouvelle migration qui touche une table avec un identifiant UUID.
- **Bien vérifier le contenu de chaque fichier créé avant de passer au suivant.** Sur ce projet, le contenu du fichier de schémas (`app/schemas/offre.py`) a été collé par erreur dans le fichier du modèle (`app/models/offre.py`), et le vrai fichier de schéma n'a jamais été créé — ce qui a fait perdre du temps à déboguer une migration vide puis une erreur d'import, alors que la cause racine était une simple confusion de fichiers en cours de copier-coller.
- **Ajouter une valeur à un `Enum` PostgreSQL existant n'est jamais détecté par l'autogénération d'Alembic.** Contrairement à l'ajout d'une colonne ou d'une table, il faut toujours ajouter manuellement une ligne `op.execute("ALTER TYPE nom_enum ADD VALUE IF NOT EXISTS 'nouvelle_valeur'")` dans le fichier de migration généré.
- **Si une migration a déjà été marquée comme "appliquée" par Alembic avant d'y ajouter une modification manuelle, `alembic upgrade head` ne rejouera pas cette modification.** Dans ce cas, il faut soit exécuter le SQL manquant directement en base (comme cela a été fait une fois pour rattraper une valeur d'enum oubliée), soit downgrader puis re-upgrader la migration concernée. Toujours vérifier `alembic current` en cas de doute sur ce qui a réellement été appliqué.
- **Après avoir ajouté un nouveau paramètre à une fonction partagée (comme `calculer_score_matching`), il faut vérifier TOUS les endroits qui l'appellent, pas seulement celui qu'on a en tête.** Un des trois routeurs qui appellent le moteur de matching avait été oublié lors de l'ajout du TJM, ce qui a fait échouer silencieusement le nouveau calcul sans lever d'erreur (la fonction utilisait juste ses valeurs par défaut). Une recherche du nom de la fonction dans tout le projet avant de considérer une modification "terminée" aurait évité ce détour.

---

## 5. Pour reprendre rapidement la prochaine fois

1. Relance la base PostgreSQL locale (pgAdmin4) et le backend :
   ```
   cd backend
   .\venv\Scripts\Activate.ps1
   uvicorn app.main:app --reload
   ```
2. Va sur `http://localhost:8000/docs`, ou lance les scripts de test existants (`test-matching.ps1`, `test-filtres.ps1`, `test-ecole.ps1`, `test-freelance.ps1`) pour valider rapidement que tout fonctionne toujours après une modification.
3. Le prochain chantier logique est l'**étape 9** du cahier des charges : les notifications in-app et email sur les événements clés (candidature reçue, changement de statut, nouvelle offre correspondant au profil). Resend est déjà installé mais jamais branché à une vraie route.
4. **Rappel méthodologique** pour chaque nouveau modèle de données à venir : (1) créer le modèle dans `app/models/`, (2) l'ajouter à l'import dans `alembic/env.py`, (3) créer le schéma dans `app/schemas/`, (4) créer les routes, (5) générer la migration et **vérifier son contenu avant de l'appliquer** (elle ne doit jamais contenir juste `pass`, et l'import de `GUID` doit être ajouté à la main si la table utilise des UUID).