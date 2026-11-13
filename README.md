# GVPDact4_t4 — CulturaVibe: API REST con Spring Security y JWT

Act4. API REST con Spring Boot, Spring Security y JWT (sin frontend).
Programación Web, equipo 13.

**Autora:** Perla Danae Gallardo Vasquez (GVPD)

## Enlaces en vivo (VPS)

- API base: http://13.140.226.55:8085
- Registro: `POST http://13.140.226.55:8085/api/auth/register`
- Login: `POST http://13.140.226.55:8085/api/auth/login`
- Act1 (sigue corriendo): http://13.140.226.55:8081/api/proyecto
- Act2 (sigue corriendo): http://13.140.226.55:8083/eventos
- Act3 (sigue corriendo): http://13.140.226.55:8084/api/eventos

## Qué se construyó

API REST pura (solo JSON, sin vistas Thymeleaf) sobre la entidad de la
Act3. Autenticación real con Spring Security y JWT:

- **Entidades:** `Evento` (14 campos, con `@ManyToOne` a `Categoría`,
  llave foránea `categoria_id`) y `Categoria`, reutilizadas de la Act3,
  más `Usuario` (nombre, correo único y contraseña con BCrypt) para la
  autenticación. Al consultar un evento, la respuesta trae el nombre de
  su categoría (`categoriaNombre`), no solo su id.
- **Registro y login** (`/api/auth/register` y `/api/auth/login`) que
  regresan un JWT real, válido 24 horas. La contraseña nunca se expone
  en ninguna respuesta (`@JsonIgnore` en la entidad y DTOs separados).
- **Endpoints protegidos:** todo `/api/eventos` y `/api/categorias`
  exige un token válido en el encabezado `Authorization: Bearer <token>`.
  Sin token, o con un token inválido o vencido, la API responde **401**.
- **DTOs y validación:** `EventoDTO`, `CategoriaDTO`, `RegistroDTO` y
  `LoginDTO` con `@Valid` (`@NotBlank`, `@Email`, `@Size`); errores de
  validación con 400, no encontrado con 404, correo ya registrado con 409.
- **Base de datos:** MySQL real, base propia `culturavibe_act4` (distinta
  a la de la Act3). Credenciales y secreto JWT por variables de entorno.
- **Puerto 8085** en el VPS, sin afectar Act1 (8081), Act2 (8083) ni
  Act3 (8084).

## Endpoints

### Autenticación (`/api/auth`, pública)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| POST | `/api/auth/register` | Registrar usuario (nombre, email, password) | 201 + token |
| POST | `/api/auth/login` | Iniciar sesión (email, password) | 200 + token |

### Eventos (`/api/eventos`, con token)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/eventos?page=0&size=12` | Listar con paginación | 200 / 401 sin token |
| GET | `/api/eventos/categoria/{categoriaId}` | Listar por categoría | 200 / 401 |
| GET | `/api/eventos/{id}` | Ver un evento | 200 / 404 / 401 |
| POST | `/api/eventos` | Crear un evento | 201 / 400 / 401 |
| PUT | `/api/eventos/{id}` | Actualizar un evento | 200 / 404 / 401 |
| DELETE | `/api/eventos/{id}` | Eliminar un evento | 204 / 404 / 401 |

### Categorías (`/api/categorias`, con token)

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/categorias` | Listar categorías | 200 / 401 |
| GET | `/api/categorias/{id}` | Ver una categoría | 200 / 404 / 401 |
| POST | `/api/categorias` | Crear una categoría | 201 / 400 / 401 |
| PUT | `/api/categorias/{id}` | Actualizar una categoría | 200 / 404 / 401 |
| DELETE | `/api/categorias/{id}` | Eliminar una categoría | 204 / 404 / 409 si tiene eventos / 401 |

## Probar con Bruno (obligatorio para esta actividad)

La colección está en `/bruno` (entornos Local y VPS). El orden es:

1. **Auth → Registro** (201, guarda el token en la variable `{{token}}`).
2. **Auth → Login** (200, actualiza `{{token}}`).
3. **Eventos →** Listar, Ver, Crear (201), Actualizar (200), Eliminar (204).
4. **Categorías →** Listar, Crear (201).
5. **Auth → Sin token** (debe dar **401**, con su prueba automática).

En Bruno, primero corre Registro o Login para que `{{token}}` tenga valor;
las demás peticiones lo usan como `Authorization: Bearer {{token}}`.

## Capturas de pantalla

### Registro (201)

![Registro en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20215713.png)

### Login (200)

![Login en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20215723.png)

### Listar eventos con token (200)

![Listado de eventos en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20215702.png)

### Crear evento con token (201)

![Crear evento en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20215815.png)

### Actualizar evento con token (200)

![Actualizar evento en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20215850.png)

### Eliminar evento con token (204)

![Eliminar evento en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20215927.png)

### Sin token (401)

![Petición sin token en Bruno](docs/Captura%20de%20pantalla%202026-11-12%20220007.png)

## Cómo correrlo en local

Requisitos: Java 17 y MySQL corriendo (o usar el perfil `test` con H2 en
memoria, solo para probar sin MySQL).

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.1"
.\mvnw.cmd clean package
```

Con MySQL local (base `culturavibe_act4`, se crea sola):

```powershell
$env:DB_PASSWORD="tu_contrasena"
.\mvnw.cmd spring-boot:run
```

Solo para probar sin MySQL (perfil `test`, H2 en memoria):

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

La API queda en http://localhost:8085.

## Despliegue en el VPS (puerto 8085)

En el VPS (bash), sin tocar las actividades anteriores:

```bash
sudo ufw allow 8085/tcp
sudo mkdir -p /opt/springboot-act4
```

En local (PowerShell), compilar y subir el jar:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.1"
.\mvnw.cmd clean package
scp .\target\gvpdact4-t4-0.0.1-SNAPSHOT.jar root@13.140.226.55:/opt/springboot-act4/
```

En el VPS, correrlo con las variables de entorno (la contraseña de MySQL
y el secreto JWT; el secreto se genera con
`openssl rand -base64 32 | tr '+/' '-_' | tr -d '='`):

```bash
cd /opt/springboot-act4
export DB_PASSWORD="tu_contrasena"
export JWT_SECRET="tu_secreto_jwt"
nohup java -jar gvpdact4-t4-0.0.1-SNAPSHOT.jar > act4.log 2>&1 &
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8085/api/eventos
```

Ese último `curl` debe dar **401** (sin token), lo que confirma que el
servidor está arriba y que el endpoint está protegido. Con token, el
mismo endpoint da 200.

