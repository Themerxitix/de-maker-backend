# De Maker - Autogarage API

REST API voor autogarage "De Maker" -

## Tech Stack

Java 17 | Spring Boot 3.x | Spring Security (JWT) | PostgreSQL | Maven

### Vereisten
- Java 17+
- PostgreSQL 14+ 
- IntelliJ IDEA 
- 
## Installatie

### 1. Clone de repository:
git clone https://github.com/Themerxitix/de-maker-backend

### 2. Database Setup 
```sql
CREATE DATABASE demaker;
```

### 3. Project Starten
```src/main/resources/application.properties 
spring.datasource.url=jdbc:postgresql://localhost:5433/demaker
spring.datasource.username=jouw_gebruikersnaam
spring.datasource.password=jouw_wachtwoord
```
### 4. Project Starten

mvn spring-boot:run

of

**Open in IntelliJ → Klik op groene**

API draait op: `http://localhost:8080`
 
## Authenticatie

**Test credentials:** Zie Installatiehandleiding voor inloggegevens.

## Testen

DeMaker Autogarage API.postman_collection_V5.json


---

**GitHub:** [github.com/Themerxitix/de-maker-backend](https://github.com/Themerxitix/de-maker-backend)