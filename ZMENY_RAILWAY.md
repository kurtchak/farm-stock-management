# Zhrnutie zmien pre Railway deployment

## Identifikované problémy

### 1. ❌ Backend sa nepripája k databáze
**Príčina:** `DatabaseConfig.java` očakával individuálne environment variables (`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`), ale Railway poskytuje `DATABASE_URL` ako jeden connection string v formáte `postgresql://user:password@host:port/database`.

**Riešenie:** 
- ✅ Upravený `DatabaseConfig.java` aby automaticky parsoval `DATABASE_URL` ak je dostupný
- ✅ Fallback na individuálne premenné ak `DATABASE_URL` nie je nastavený
- ✅ Správne parsovanie hesiel so špeciálnymi znakmi

### 2. ❌ Frontend nevidí backend
**Príčina:** 
- `nginx.conf` mal natvrdo zadaný backend URL `http://farm-stock-management:8080/api/` ktorý nefunguje na Railway
- Frontend Dockerfile nepodporoval environment variables

**Riešenie:**
- ✅ `nginx.conf` teraz používa environment variable `BACKEND_URL`
- ✅ Vytvorený `docker-entrypoint.sh` ktorý nastaví default hodnotu pre Railway
- ✅ Frontend Dockerfile upravený aby podporoval environment variables cez `envsubst`

### 3. ❌ Railway konfigurácia
**Príčina:** 
- `railway.json` bol nastavený len pre frontend
- Backend nemal Railway konfiguráciu

**Riešenie:**
- ✅ Vytvorený `backend/railway.json` pre backend service
- ✅ Aktualizovaný root `railway.json` pre frontend

### 4. ❌ Dockerfile syntax
**Príčina:** Backend Dockerfile mal nesprávnu syntax pre Java argumenty

**Riešenie:**
- ✅ Opravená syntax v `backend/Dockerfile` - `-Dspring.profiles.active=prod` je teraz pred `-jar`

### 5. ❌ Chýbajúca Maven závislosť
**Príčina:** `AdminNotificationService.java` používa `JavaMailSender`, ale v `pom.xml` chýbala závislosť `spring-boot-starter-mail`

**Riešenie:**
- ✅ Pridaná závislosť `spring-boot-starter-mail` do `pom.xml`
- ✅ `JavaMailSender` je teraz optional pomocou `@Autowired(required = false)`

## Zmenené súbory

1. `backend/src/main/java/com/farmstock/config/DatabaseConfig.java` - Parsovanie DATABASE_URL
2. `frontend/nginx.conf` - Environment variable pre backend URL
3. `frontend/Dockerfile` - Podpora environment variables
4. `frontend/docker-entrypoint.sh` - Nový startup script
5. `backend/Dockerfile` - Opravená syntax
6. `backend/railway.json` - Nový Railway config pre backend
7. `railway.json` - Aktualizovaný pre frontend
8. `backend/pom.xml` - Pridaná závislosť spring-boot-starter-mail
9. `backend/src/main/java/com/farmstock/service/AdminNotificationService.java` - Optional JavaMailSender
10. `RAILWAY_DEPLOYMENT.md` - Nová dokumentácia

## Ďalšie kroky

1. **Na Railway:**
   - Vytvorte PostgreSQL service (Railway automaticky vytvorí `DATABASE_URL`)
   - Vytvorte backend service s root directory `backend`
   - Vytvorte frontend service s root directory `frontend`
   - V frontend service nastavte `BACKEND_URL` environment variable:
     - Pre internal networking: `http://backend:8080` (ak má backend service meno "backend")
     - Alebo použite public URL backend service

2. **Environment Variables:**
   - Backend: `DATABASE_URL` (automaticky z PostgreSQL service), `JWT_SECRET`, atď.
   - Frontend: `BACKEND_URL` (nastavte manuálne)

3. **Verifikácia:**
   - Skontrolujte backend logs - mali by ste vidieť úspešné pripojenie k databáze
   - Skontrolujte že Flyway migrations bežia
   - Testujte API endpointy
   - Skontrolujte frontend - mal by sa pripájať k backendu

## Dôležité poznámky

- Railway automaticky poskytuje `DATABASE_URL` keď pridáte PostgreSQL service
- Backend teraz automaticky parsuje `DATABASE_URL` - žiadne ďalšie konfigurácie nie sú potrebné
- Frontend potrebuje `BACKEND_URL` environment variable - nastavte ho podľa vašej Railway konfigurácie
- Test data sú v migration `V2__Insert_Test_Data.sql` - mali by sa automaticky načítať pri prvom spustení
- Email notifikácie sú voliteľné - aplikácia funguje aj bez mail konfigurácie
