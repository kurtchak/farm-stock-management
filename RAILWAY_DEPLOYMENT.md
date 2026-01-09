# Railway Deployment Guide

## Problémy ktoré boli opravené

### 1. Database Connection
**Problém:** Backend očakával individuálne environment variables (`PGHOST`, `PGPORT`, atď.), ale Railway poskytuje `DATABASE_URL` ako jeden connection string.

**Riešenie:** `DatabaseConfig.java` teraz automaticky parsuje `DATABASE_URL` ak je dostupný, inak použije individuálne premenné.

### 2. Frontend-Backend Communication
**Problém:** Nginx mal natvrdo zadaný backend URL, ktorý nefungoval na Railway.

**Riešenie:** 
- `nginx.conf` teraz používa environment variable `BACKEND_URL`
- `docker-entrypoint.sh` nastaví default hodnotu pre Railway internal networking
- Frontend Dockerfile podporuje environment variables

### 3. Railway Configuration
**Problém:** `railway.json` bol nastavený len pre frontend, backend nebol nakonfigurovaný.

**Riešenie:** 
- Vytvorený `backend/railway.json` pre backend service
- Aktualizovaný root `railway.json` pre frontend

## Postup nasadenia na Railway

### 1. Vytvorenie služieb na Railway

1. Vytvorte nový projekt na Railway
2. Pridajte PostgreSQL databázu (Railway automaticky vytvorí `DATABASE_URL`)
3. Vytvorte **backend service**:
   - Connect repository
   - Root Directory: `backend`
   - Railway automaticky detekuje `Dockerfile` alebo `railway.json`
4. Vytvorte **frontend service**:
   - Connect repository (alebo použite ten istý repo)
   - Root Directory: `frontend`
   - Railway automaticky detekuje `Dockerfile`

### 2. Konfigurácia Environment Variables

#### Backend Service:
- `SPRING_PROFILES_ACTIVE=prod` (nastavené automaticky v Dockerfile)
- `DATABASE_URL` - Railway automaticky vytvorí z PostgreSQL service
- `JWT_SECRET` - nastavte silný secret
- Ostatné voliteľné (SMS, admin notifications, atď.)

#### Frontend Service:
- `BACKEND_URL` - URL backend service na Railway
  - Ak sú služby v tom istom projekte: `http://backend:8080`
  - Alebo použite public URL backend service: `https://your-backend-service.railway.app`

### 3. Service Discovery na Railway

Na Railway môžete použiť:
- **Internal networking:** `http://backend:8080` (ak má backend service meno "backend")
- **Public URL:** Použite Railway-generated URL pre backend service

### 4. Verifikácia

1. Skontrolujte backend logs - mali by ste vidieť úspešné pripojenie k databáze
2. Skontrolujte že Flyway migrations bežia (v backend logs)
3. Testujte backend API endpointy
4. Skontrolujte frontend - mal by sa pripájať k backendu

## Troubleshooting

### Backend sa nepripája k databáze
- Skontrolujte že `DATABASE_URL` je nastavený v backend service
- Skontrolujte backend logs pre chyby pripojenia
- Verifikujte že PostgreSQL service beží

### Frontend nevidí dáta
- Skontrolujte že `BACKEND_URL` je správne nastavený
- Skontrolujte nginx logs v frontend service
- Testujte backend API priamo (cez public URL)

### Migrations nebežia
- Skontrolujte že `DATABASE_URL` je správne parsovaný
- Skontrolujte backend logs pre Flyway správy
- Verifikujte že migration súbory sú v `backend/src/main/resources/db/migration/`

## Lokálne testovanie

Pre lokálne testovanie s Railway databázou:

```bash
export DATABASE_URL="postgresql://user:pass@host:port/database"
cd backend
mvn clean package -DskipTests
java -jar target/*.jar -Dspring.profiles.active=prod
```

