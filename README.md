# De Maker - Autogarage API

REST API voor autogarage "De Maker" -

## Tech Stack

Java 17 | Spring Boot 3.x | Spring Security (JWT) | PostgreSQL | Maven

## Installatie

### Vereisten
- Java 17+
- PostgreSQL 14+ (moet draaien)
- IntelliJ IDEA (aanbevolen)

### 1. Database Setup (Eerst!)
```sql
CREATE DATABASE garage_db;
CREATE USER garage_user WITH PASSWORD '';
GRANT ALL PRIVILEGES ON DATABASE garage_db TO garage_user;
```

### 2. Project Starten
```bash
git clone https://github.com/Themerxitix/de-maker-backend.git
```

**Open in IntelliJ → Klik op groene ▶ knop rechtsboven**

API draait op: `http://localhost:8080`

## Authenticatie

De API gebruikt JWT tokens voor beveiliging. Twee gebruikersrollen:
| Rol     | Username   | Password    |
|---------|------------|-------------|
| Admin   | `admin`    | `admin123`  |
| Monteur | `monteur1` | `monteur123`|

**Test credentials:** Zie Installatiehandleiding voor inloggegevens.

## Testen

**IntelliJ:** Rechtermuis op `test` folder → Run Tests
- 21 unit tests
- 100% line coverage op 2 service classes

**Postman:** Importeer `Demaker-API.postman_collection.json` voor API tests


---

**GitHub:** [github.com/Themerxitix/de-maker-backend](https://github.com/Themerxitix/de-maker-backend)