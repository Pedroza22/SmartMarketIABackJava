# SmartMarketIA Backend (Java)

Backend de SmartMarketIA construido con Spring Boot 3.

## Características

- Autenticación JWT con Spring Security (`/api/auth/login`, `/api/auth/register`).
- Modelos: `User`, `Product`, `Subscription`, `Analysis` y repositorios JPA.
- API REST:
  - Productos: listar/crear/editar/eliminar.
  - Suscripciones: crear y listar por usuario.
  - Análisis: solicitar análisis y ver historial.
  - Admin: métricas básicas (`/api/admin/metrics`).
- CORS configurable (`cors.allowed-origins`).
- Manejo global de errores con respuestas JSON.
- Logs en JSON (Logback + logstash encoder).
- Cliente HTTP al microservicio Python (timeouts + reintentos).
- Dockerfile y `docker-compose.yml` para levantar backend + pyservice.

## Ejecutar local

1. Asegura Java 17 y Maven.
2. Configura variables (opcional):
   - `PY_SERVICE_BASE_URL` (por defecto `http://localhost:8001`).
   - `CORS_ALLOWED_ORIGINS`.
   - `JWT_SECRET`.
3. `mvn spring-boot:run`

H2 Console: `http://localhost:8080/h2-console`

## Docker

- Construir y correr backend:
  ```bash
  docker build -t smartmarket-backend .
  docker run -p 8080:8080 -e PY_SERVICE_BASE_URL=http://localhost:8001 smartmarket-backend
  ```

- Orquestar backend + pyservice:
  ```bash
  docker compose up --build
  ```

## Endpoints principales

- `/api/auth/register`, `/api/auth/login`
- `/api/users/me`
- `/api/products` (GET, POST, PUT, DELETE; POST/PUT/DELETE requieren `ADMIN`)
- `/api/subscriptions` (POST), `/api/subscriptions/me` (GET)
- `/api/analyses` (POST), `/api/analyses/me` (GET), `/api/analyses/admin/all` (GET `ADMIN`)
- `/api/admin/metrics` (GET `ADMIN`)

## Notas

- Cambia `JWT_SECRET` en producción.
- Revisa `application.yml` para más configuración.