# 02 - Spring Security: Basic, JWT y OAuth2

## HTTP Basic

HTTP Basic es una forma de autenticación en donde el cleinte envia su usuario y contraseña en cada petición HTTP para demostrar quien es.

Funciona de manera que al acceder a un endpoint, por ejemplo `GET /api/productos` se tiene que enviar en la cabecera el usuario como Authorization, este se envia codificado en Base64. 

El servidor recibe la peticion, busca al usuario y obtiene su contraseña encriptada almacenada, comproueba si coincide con el hash y si es correcta permite acceder, de lo contrario responde un `401 Unauthorized`.

```
Cliente
   │
   │ GET /api/productos
   │ Authorization: Basic ...
   ▼
Spring Security
   │
   ├── ¿Usuario existe?
   │
   ├── ¿Password correcta?
   │
   ├── NO ──────────► 401 Unauthorized
   │
   └── SÍ
        │
        ▼
      API
        │
        ▼
      200 OK
````

> NOTA: Basic no cifra las credenciales, Base64 solo las codifica, es por eso que HTTP Basic debe utilizarse junto con HTTPS para que alguien no pueda interceptar las credenciales


### Como probar en el proyecto

1. Con HTTP Basic, simplemente hacemos una request mandando las credenciales

```
curl -u john:test123  http://localhost:8074/api/basic/employees
curl -u susan:test123 -X DELETE http://localhost:8074/api/basic/employees/1
```

## JWT 

JWT (JSON Web Token) es un formato de token que permite que una aplicación identifique y autorice a un usuario despues de que se haya identificado.

El usuario inicia sesion una vez, despues el servidor le entrega un token con el que el usaurio puede acceder a recursos protegidos siempre y cuando tenga autorizacion. A diferencia de HTTP Basic, ya no es necesario que el usaurio presente sus credenciales en cada peticion, pues ahora tiene una llave para hacerlo (con fecha de expiracion).

### Diferencia

```
HTTP Basic
────────────────────────────
Cada petición
      ↓
usuario + contraseña
      ↓
servidor


JWT
────────────────────────────
Login
  ↓
usuario + contraseña
  ↓
servidor
  ↓
JWT
  ↓
Cada petición
  ↓
JWT
  ↓
servidor

```
Funciona de manera que si tienes un usuario **root** y una contraseña **admin**, primero haces un
`POST /api/auth/login`

```
{
  "username": "root",
  "password": "admin"
}
```
Y si las llaves son correctas, el servidor genera un JWT:

> eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuIiwicm9sZSI6IlVTRVIifQ....

El cliente guarda el token y despues lo usa para acceder a un recurso protegido:

```
GET /api/productos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
El servidor lo recibe, lo valida, y si es valido le permite acceder:
```
Cliente
   │
   │ Authorization: Bearer JWT
   ▼
Spring Security
   │
   ├── ¿El token tiene una firma válida?
   ├── ¿Está vencido?
   ├── ¿Es válido para esta aplicación?
   └── ¿Qué usuario/roles contiene?
          │
          ▼
       API
          │
          ▼
       200 OK
```
Estructura del token JWT:
`HAEDER.PAYLOAD.SIGNATURE`
- Header: indica informacion sobre el token como el algoritmo utilizado
- Payload: contiene la informacion o claims, sobre el usuario o el token, como la expiracion, usuario, el rol y cuando fue emitido
- Signature: es la firma que garantiza que el token no ha sido alterado

### Como probar en el proyecto

Requisitos: 
Java 21 y Maven (o el wrapper ./mvnw).
MySQL con la base employee_directory, el usuario springstudent y las tablas members/roles

1. Iniciar sesion (pedir token)

```
TOKEN=$(curl -s -u mary:test123 -X POST http://localhost:8074/api/auth/login \
        | python3 -c "import json,sys;print(json.load(sys.stdin)['accessToken'])")
```

2. Acceder a recursos con ese token
```
curl -H "Authorization: Bearer $TOKEN" http://localhost:8074/api/jwt/employees
```

## OAuth2

 OAuth2 permite que una aplicacion delegue la autorizacion de un usuario en otro sistema.
 Permite que una aplicacion obtenga acceso a recursos de un usuario sin que el usuario tenga que entregar su contraseña a esa aplicacion.

 La ventaja de OAuth2 es su simplicidad a la hora de autenticarse pues puedes hacerlo con proveedores que te facilitan el proceso. Por ejemplo en una aplicacion web, poder registrarte con tus credenciales de Google, Facebook o Github.

Por ejemplo, un usuario **juan** intenta acceder a la app y esta lo redirige a **Keycloak**, despues juan introduce su usuario y contraseña, keycloak valida las credenciales y devuelve un token. La aplicacion posteriormente utiliza ese token para acceder a los recursos protegidos 


 **En el proyecto demostracion** usamos keycloak, el cual es un identity and access management (IAM) de codigo abierto para centralizar la identidad. 



 ```
 Usuario
   ↓
Keycloak
   ↓
Autentica al usuario
   ↓
Genera un token
   ↓
Nuestra aplicación
   ↓
Accede a la API protegida
 ```

 Para que Keycloak funcione con nuestra aplicación, principalmente debemos configurar:

- **Realm**: espacio donde se gestionan los usuarios, roles y configuraciones.
- **Users**: usuarios que podrán autenticarse.
- **Roles**: permisos que tendrán los usuarios, por ejemplo USER o ADMIN.
- **Client**: nuestra aplicación Spring Boot registrada en Keycloak.
- **Redirect URI**: URL a la que Keycloak devolverá al usuario después del login.
- **Tokens**: Keycloak se encarga de generarlos y validarlos según la configuración.

**En resumen:**
Keycloak se encarga de gestionar la identidad del usuario, autenticarlo, gestionar sus roles y emitir los tokens que nuestra aplicación utilizará para permitir o denegar el acceso.

### Como probarlo en el proyecto

1. Correr **keycloak** en el puerto 8090

2. crear realm, client, roles y usuarios (primera vez), correr:
```
./scripts/keycloak-setup.sh
```

3. Pedir token a keycloak y acceder a recursos
```
TOKEN=$(curl -s -X POST http://localhost:8090/realms/academy/protocol/openid-connect/token \
        -d grant_type=password -d client_id=employee-api -d username=john -d password=test123 \
        | python3 -c "import json,sys;print(json.load(sys.stdin)['access_token'])")
```
```
curl -H "Authorization: Bearer $TOKEN" http://localhost:8074/api/oauth2/employees
curl -H "Authorization: Bearer $TOKEN" http://localhost:8074/api/oauth2/me
```

# Resumen del proyecto
| Mecanismo   | Endpoint                            | Como se autentica                                  |
| ----------- | ----------------------------------- | -------------------------------------------------- |
| HTTP Basic  | `/api/basic/...`                    | Usuario + contrasena en cada peticion              |
| JWT         | `/api/auth/login` → `/api/jwt/...`  | Login -> token -> acceso con `Bearer <token>`      |
| OAuth2      | `/api/oauth2/...`                   | Login delegado a Keycloak (token validado ajeno)   |

