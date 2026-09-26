# 🏆 Plateforme de gestion de Hackathon

Application web complète de gestion d'un hackathon : inscription, gestion des équipes, soumission de projets, évaluation par un jury et classement en temps réel.

Projet réalisé en 2 semaines avec **Spring Boot** (backend) et **Angular** (frontend).

---

## 📹 Démonstration vidéo

👉 [Voir la démonstration sur Google Drive](LIEN_DRIVE_À_COMPLÉTER)

## 📄 Rapport de projet

Le rapport PDF complet (architecture, modèle de données, sécurité, API, fonctionnalités bonus) se trouve dans ce dépôt : [`rapport_hackathon.pdf`](./rapport_hackathon.pdf)

---

## 🧱 Stack technique

**Backend**
- Spring Boot 4 (Spring Web, Spring Security, Spring Data JPA)
- JWT pour l'authentification (jjwt)
- WebSocket (STOMP) pour le classement en temps réel
- Swagger / OpenAPI pour la documentation de l'API
- MySQL

**Frontend**
- Angular (composants standalone, Signals, RxResource)
- Route Guards, HTTP Interceptor JWT
- Environments (dev / prod)
- STOMP.js pour la connexion temps réel

---

## 👥 Acteurs

| Rôle | Actions principales |
|---|---|
| **Participant** | S'inscrire, créer/rejoindre une équipe, soumettre un projet (+ fichier joint), consulter le classement |
| **Jury** | Consulter les projets, attribuer une note (innovation / technique / présentation) |
| **Administrateur** | Gérer les utilisateurs, les équipes, les projets, définir la deadline de soumission |

---

## ✨ Fonctionnalités

### Cœur du cahier des charges
- Authentification JWT + gestion des rôles (`ROLE_PARTICIPANT`, `ROLE_JURY`, `ROLE_ADMIN`)
- Gestion des équipes (créer / rejoindre / quitter / lister)
- Gestion des projets (soumettre / modifier / consulter)
- Évaluation des projets par le jury (3 critères de notation)
- Classement automatique des équipes (leaderboard)
- Panel d'administration (utilisateurs, équipes, projets)
- Documentation API via Swagger

### Bonus
- ⏰ **Deadline de soumission** configurable par l'administrateur
- 🥇 **Badge gagnant** pour l'équipe en tête du classement
- 🔔 **Notifications** (arrivée/départ d'un membre, projet évalué)
- 📎 **Upload de fichier** attaché au projet (en plus du lien GitHub)
- ⚡ **Temps réel (WebSocket)** — le classement se met à jour instantanément chez tous les utilisateurs connectés, sans rechargement de page

---

## 🔐 Sécurité

- Mots de passe hachés avec BCrypt (jamais stockés en clair)
- Authentification stateless par JWT (`Authorization: Bearer <token>`)
- Contrôle d'accès par rôle au niveau des routes (Spring Security) **et** au niveau des données (vérifications métier dans les services)
- Gestion centralisée des erreurs, réponses JSON uniformes

---

## 🗂️ Structure du dépôt

```
├── backend/          → Application Spring Boot (API REST)
├── frontend/          → Application Angular
├── rapport_hackathon.pdf
└── README.md
```

---

## ⚙️ Lancer le projet en local

### Prérequis
- Java 17+
- Node.js + Angular CLI
- MySQL

### Backend
```bash
cd backend
# configurer application.properties (URL base de données, port MySQL)
mvn spring-boot:run
```
API disponible sur `http://localhost:8080`
Documentation Swagger : `http://localhost:8080/swagger-ui/index.html`

### Frontend
```bash
cd frontend
npm install
ng serve
```
Application disponible sur `http://localhost:4200`

---

## 📚 API — aperçu des routes principales

| Domaine | Exemples de routes |
|---|---|
| Authentification | `POST /api/auth/register`, `POST /api/auth/login` |
| Équipes | `POST /api/teams`, `POST /api/teams/{id}/join`, `GET /api/teams` |
| Projets | `POST /api/projects`, `PUT /api/projects/{id}`, `GET /api/projects` |
| Évaluation | `POST /api/jury/evaluations/{projectId}`, `GET /api/jury/projects` |
| Classement | `GET /api/leaderboard` |
| Administration | `GET /api/admin/users`, `PUT /api/admin/settings/deadline` |

Liste complète et testable dans Swagger.

---

## 👤 Auteur

Projet réalisé par **Amadou Sadikh** dans le cadre d'un exercice pratique Spring Boot / Angular.
