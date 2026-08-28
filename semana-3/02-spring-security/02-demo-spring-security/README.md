# 04-security-combined

Proyecto que junta los **tres mecanismos de autenticacion** de los proyectos
anteriores en **una sola aplicacion** Spring Boot (Spring Security 7):

| Mecanismo   | Endpoint                            | Como se autentica                                  |
| ----------- | ----------------------------------- | -------------------------------------------------- |
| HTTP Basic  | `/api/basic/...`                    | Usuario + contrasena en cada peticion              |
| JWT         | `/api/auth/login` → `/api/jwt/...`  | Login -> token -> acceso con `Bearer <token>`      |
| OAuth2      | `/api/oauth2/...`                   | Login delegado a Keycloak (token validado ajeno)   |

## La idea central

Los tres mecanismos terminan produciendo lo **mismo** para Spring: un usuario
autenticado con unos roles. Por eso el **control de acceso es identico** sobre
`/basic`, `/jwt` y `/oauth2` (empleados leen, managers crean/modifican, solo
admin borra).

Lo que cambia es **como** llegas a ese estado. Esa diferencia vive toda en
`SecurityConfig.java`, con **cinco cadenas de filtros** (`@Order`):

| `@Order` | Ruta            | Autenticacion        | De donde salen los usuarios/roles        |
| -------- | --------------- | -------------------- | ---------------------------------------- |
| 1        | `/api/auth/**`  | HTTP Basic           | tablas `members`/`roles` (MySQL)         |
| 2        | `/api/basic/**` | HTTP Basic           | tablas `members`/`roles` (MySQL)         |
| 3        | `/api/jwt/**`   | Bearer JWT (propio)  | claim `roles` del token RSA local        |
| 4        | `/api/oauth2/**`| Bearer JWT (Keycloak)| `realm_access.roles` del token de Keycloak |
| 5        | (resto)         | HTTP Basic           | red de seguridad por defecto             |

Prueba a descomentar `logging.level...=DEBUG` en `application.properties` y
mira los logs: veras como una URL coincide con una sola cadena.

## Requisitos

- Java 21 y Maven (o el wrapper `./mvnw`).
- MySQL con la base `employee_directory`, el usuario `springstudent` y las
  tablas `members`/`roles` (ver `../sql-scripts/01-security-tables.sql` e
  `../instalacion.txt`).
- Keycloak en el puerto 8090 **solo para el bloque OAuth2** (según
  `../03-security-oauth2/scripts/keycloak-setup.sh`). Los bloques Basic y JWT
  no necesitan Keycloak.

## Como correr

```bash
./mvnw spring-boot:run        # o: mvn spring-boot:run
# la app escucha en http://localhost:8074
```

## Como probar

```bash
./scripts/test-endpoints.sh    # matriz completa de los 3 mecanismos
```

### HTTP Basic (`/api/basic`)
```bash
curl -u john:test123  http://localhost:8074/api/basic/employees
curl -u susan:test123 -X DELETE http://localhost:8074/api/basic/employees/1
```

### JWT (`/api/auth/login` + `/api/jwt`)
```bash
TOKEN=$(curl -s -u mary:test123 -X POST http://localhost:8074/api/auth/login \
        | python3 -c "import json,sys;print(json.load(sys.stdin)['accessToken'])")
curl -H "Authorization: Bearer $TOKEN" http://localhost:8074/api/jwt/employees
```

### OAuth2 (`/api/oauth2`, token de Keycloak)
```bash
TOKEN=$(curl -s -X POST http://localhost:8090/realms/academy/protocol/openid-connect/token \
        -d grant_type=password -d client_id=employee-api -d username=john -d password=test123 \
        | python3 -c "import json,sys;print(json.load(sys.stdin)['access_token'])")
curl -H "Authorization: Bearer $TOKEN" http://localhost:8074/api/oauth2/employees
curl -H "Authorization: Bearer $TOKEN" http://localhost:8074/api/oauth2/me
```

## Usuarios de practica

`john` (EMPLOYEE), `mary` (+MANAGER), `susan` (+ADMIN). Todos con password
`test123`. John solo lee; mary lee, crea y modifica; solo susan borra.
