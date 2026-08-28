#!/bin/bash
# Matriz de seguridad de 04-security-combined (Basic + JWT + OAuth2 en UNA app)
# API en 8074. Keycloak en 8090 (solo para el bloque OAuth2).
#
# Los 3 usuarios comparten la password: test123
#   john   ROLE_EMPLOYEE
#   mary   ROLE_EMPLOYEE + ROLE_MANAGER
#   susan  ROLE_EMPLOYEE + ROLE_MANAGER + ROLE_ADMIN
#
# La leccion de este proyecto: los TRES mecanismos aplican EXACTAMENTE las mismas
# reglas de acceso. Cambia como te autenticas, no que puedes hacer.

BASE="http://localhost:8074/api"
BASIC="$BASE/basic/employees"
JWT="$BASE/jwt/employees"
OAUTH="$BASE/oauth2/employees"
LOGIN="$BASE/auth/login"
KC="http://localhost:8090/realms/academy/protocol/openid-connect"

paso() { echo; echo "════════ $1 ════════"; }

probar() {  # probar DESCRIPCION ESPERADO curl-args...
  local desc=$1 esp=$2; shift 2
  local code=$(curl -s -o /dev/null -w '%{http_code}' "$@")
  local marca="!!"; [ "$code" = "$esp" ] && marca="OK"
  printf "%s  %-52s -> HTTP %s  (esperado %s)\n" "$marca" "$desc" "$code" "$esp"
}

# ---------------------------------------------------------------
# 1. HTTP BASIC  (proyecto 01)
# ---------------------------------------------------------------
paso "1. HTTP BASIC -> /api/basic/**  (credenciales en CADA peticion)"
probar "john  GET  basic (EMPLOYEE)"   200 -u john:test123  "$BASIC"
probar "john  POST basic -> 403"       403 -u john:test123 -X POST "$BASIC" -H "Content-Type: application/json" -d '{"firstName":"X","lastName":"Y","email":"x@y.com"}'
probar "john  DELETE basic -> 403"     403 -u john:test123 -X DELETE "$BASIC/1"
probar "sin credenciales -> 401"       401 "$BASIC"
echo "  -> La contrasena iba en 'Authorization: Basic <base64>'."

# ---------------------------------------------------------------
# 2. JWT  (proyecto 02)
# ---------------------------------------------------------------
paso "2. JWT -> /api/auth/login + /api/jwt/**  (login una vez, luego Bearer)"
# OJO con el quoting en zsh: escribe "${1}:test123".
token() {
  curl -s -u "${1}:test123" -X POST "$LOGIN" \
    | python3 -c "import json,sys;print(json.load(sys.stdin)['accessToken'])" 2>/dev/null
}
TJ=$(token john); TM=$(token mary); TS=$(token susan)
probar "login con password mala -> 401" 401 -u john:MALA -X POST "$LOGIN"
probar "HTTP Basic contra /api/jwt    -> 401 (no sirve)" 401 -u susan:test123 "$JWT"
probar "GET  jwt con token de john    -> 200 (Bearer)"   200 -H "Authorization: Bearer $TJ" "$JWT"
probar "POST jwt con token de john    -> 403"            403 -H "Authorization: Bearer $TJ" -X POST "$JWT" -H "Content-Type: application/json" -d '{"firstName":"X","lastName":"Y","email":"x@y.com"}'
probar "DELETE jwt con token de john  -> 403"            403 -H "Authorization: Bearer $TJ" -X DELETE "$JWT/1"
echo "  -> Tras el login, la contrasena ya no viaja: solo el token."

# ---------------------------------------------------------------
# 3. OAUTH2  (proyecto 03) - requiere Keycloak en el 8090
# ---------------------------------------------------------------
paso "3. OAUTH2 -> /api/oauth2/**  (token emitido por KEYCLOAK)"
if [ -z "$(curl -s -m 3 -o /dev/null -w '%{http_code}' http://localhost:8090/realms/academy/.well-known/openid-configuration | grep 200)" ]; then
  echo "  !! Keycloak no responde en el 8090. Arrancalo y ejecuta keycloak-setup.sh."
else
  kctoken() {
    curl -s -X POST "$KC/token" -d grant_type=password -d client_id=employee-api \
      -d username="$1" -d password=test123 \
      | python3 -c "import json,sys;print(json.load(sys.stdin)['access_token'])" 2>/dev/null
  }
  KO=$(kctoken john)
  probar "GET  oauth2 con token de Keycloak -> 200" 200 -H "Authorization: Bearer $KO" "$OAUTH"
  probar "POST oauth2 con john -> 403"             403 -H "Authorization: Bearer $KO" -X POST "$OAUTH" -H "Content-Type: application/json" -d '{"firstName":"X","lastName":"Y","email":"x@y.com"}'
  echo "  -> /api/oauth2/me muestra quien viene en el token:"
  curl -s -H "Authorization: Bearer $KO" "$BASE/oauth2/me"; echo
fi

# ---------------------------------------------------------------
# 4. Las llaves están separadas: cada mecanismo usa SU decoder
# ---------------------------------------------------------------
paso "4. Un token de Keycloak NO abre /api/jwt (y viceversa)"
if [ -n "${KO:-}" ]; then
  probar "token Keycloak contra /api/jwt -> 401" 401 -H "Authorization: Bearer $KO" "$JWT"
fi
if [ -n "$(curl -s -m 2 -o /dev/null -w '%{http_code}' http://localhost:8072/api/employees 2>/dev/null)" ] && [ -n "$TJ" ]; then
  echo "  (puedes comparar ademas con el proyecto 02 corriendo en 8072)"
fi

echo
echo "════════ RESUMEN ════════"
echo "  /api/basic/...  = HTTP Basic   (contra la base, cada peticion)"
echo "  /api/auth/login = taquilla JWT (Basic una vez, devuelve token)"
echo "  /api/jwt/...    = Bearer <token propio>"
echo "  /api/oauth2/... = Bearer <token de Keycloak>"
echo "  Mismas reglas de rol en los cuatro bloques."
