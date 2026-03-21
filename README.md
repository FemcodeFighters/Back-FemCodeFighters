# ⚔️ Code Fighters — Backend

> 🇪🇸 [Español](#español)

---

## Español

### 📖 Descripción

**Code Fighters** es un videojuego de peleas de plataformas temático del mundo del desarrollo de software. Los jugadores crean su personaje programadora, personalizan su apariencia y se enfrentan contra Stacks usando habilidades definitivas inspiradas en el día a día del desarrollo: `FRIDAY DEPLOY`, `SPAGHETTI CODE STORM` y `GIT CLONE REPOSITORY`.

Este proyecto es el backend de la aplicación, construido con **Spring Boot** y expuesto mediante una **API REST** que consume el frontend desarrollado en **Phaser.js**.

---

### 🛠️ Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje principal |
| Spring Boot 3.x | Framework backend |
| Spring Security + JWT | Autenticación y autorización |
| Spring Data JPA + Hibernate | Persistencia de datos |
| PostgreSQL | Base de datos en producción |
| H2 (in-memory) | Base de datos en tests |
| MapStruct | Mapeo de entidades a DTOs |
| Lombok | Reducción de boilerplate |
| JUnit 5 + Mockito | Testing unitario |
| MockMvc | Testing de endpoints REST |
| Maven | Gestión de dependencias |

---

### ⚙️ Instalación y ejecución

#### Requisitos previos

- Java 21+
- Maven 3.8+
- PostgreSQL 15+

#### 1. Clonar el repositorio

```bash
git clone https://github.com/FemcodeFighters/Back-FemCodeFighters.git
```

#### 2. Estructura del proyecto
📦src
 ┣ 📂main
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂code
 ┃ ┃ ┃ ┃ ┗ 📂fighters
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GameBalanceConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SecurityConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserDetailsConfig.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜HealthController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserController.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂updatePlayer
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdAccessoryRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdAllRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdEyeColorRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdHairRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdOutfitRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdSkinColorRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UpdUltimateRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂updateUser
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdUserEmailRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UpdUserNameRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UpdUserPassRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜LoginRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegisterRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthResponseDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorResponseDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerResponseDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜RankingResponseDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UltimateConfigResponseDTO.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserResponseDTO.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂enums
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UltimateSkill.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Player.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜User.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜EmailAlreadyExistsException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜InvalidPasswordException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerNotFoundException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserNameAlreadyExistsException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserNotFoundException.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthMapper.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerMapper.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserMapper.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserRepository.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂security
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAuthFilter.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JwtService.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜UserService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceImpl.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜FightersApplication.java
 ┃ ┗ 📂resources
 ┃ ┃ ┣ 📂static
 ┃ ┃ ┣ 📂templates
 ┃ ┃ ┗ 📜application.properties
 ┗ 📂test
 ┃ ┣ 📂java
 ┃ ┃ ┗ 📂com
 ┃ ┃ ┃ ┗ 📂code
 ┃ ┃ ┃ ┃ ┗ 📂fighters
 ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SecurityConfigTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserDetailsConfigTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserControllerTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂mapper
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PlayerMapperTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RepositoryTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂security
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JwtServiceTest.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PlayerServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜UserServiceImplTest.java
 ┃ ┃ ┃ ┃ ┃ ┗ 📜FightersApplicationTests.java
 ┃ ┗ 📂resources
 ┃ ┃ ┗ 📜application-test.properties

#### 3. Configurar variables de entorno

Crea un archivo `.env` en la raíz del proyecto:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=codefighters
DB_USER=tu_usuario
DB_PASS=tu_contraseña
JWT_SECRET=tu_clave_secreta_de_al_menos_32_caracteres
```

#### 4. Crear la base de datos

```sql
CREATE DATABASE codefighters;
```

#### 5. Ejecutar la aplicación

```bash
./mvnw spring-boot:run
```

La API estará disponible en `http://localhost:8080`.

---

### 🔌 Endpoints de la API

#### 🔓 Autenticación — `/api/auth` (público)

| Método | Endpoint | Descripción |
|---|---|---|
| `POST` | `/api/auth/register` | Registrar nuevo usuario |
| `POST` | `/api/auth/login` | Iniciar sesión y obtener token JWT |
| `POST` | `/api/auth/logout` | Cerrar sesión |

**Ejemplo registro:**
```json
POST /api/auth/register
{
  "username": "MasterCoder",
  "email": "coder@test.com",
  "password": "Password123!"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "MasterCoder",
  "email": "coder@test.com",
  "userId": 1
}
```

---

#### 👤 Usuario — `/api/users` (🔒 requiere token)

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/users/profile` | Ver perfil del usuario |
| `GET` | `/api/users` | Listar todos los usuarios |
| `PATCH` | `/api/users/email` | Actualizar email |
| `PATCH` | `/api/users/username` | Actualizar username |
| `PATCH` | `/api/users/password` | Cambiar contraseña |
| `DELETE` | `/api/users` | Eliminar cuenta |

---

#### 🎮 Personaje — `/api/player` (🔒 requiere token)

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/player` | Ver personaje |
| `PATCH` | `/api/player/skin` | Actualizar color de piel |
| `PATCH` | `/api/player/hair` | Actualizar pelo |
| `PATCH` | `/api/player/eyes` | Actualizar color de ojos |
| `PATCH` | `/api/player/outfit` | Actualizar outfit |
| `PATCH` | `/api/player/accessory` | Actualizar accesorio |
| `PATCH` | `/api/player/ultimate` | Cambiar habilidad definitiva |
| `PATCH` | `/api/player/all` | Actualizar toda la apariencia |
| `POST` | `/api/player/reset` | Resetear personaje |
| `POST` | `/api/player/use-ultimate` | Usar habilidad definitiva |
| `POST` | `/api/player/combat-result/{won}` | Registrar resultado de combate |
| `GET` | `/api/player/ranking` | Ver top 10 jugadores |
| `GET` | `/api/player/ultimate-config` | Obtener configuración de la habilidad |

#### ⚔️ Habilidades definitivas

| Habilidad | Efecto |
|---|---|
| `FRIDAY_DEPLOY` | Cura al jugador +30 HP (máx. 100) |
| `SPAGHETTI_CODE` | Inflige 5 de daño a todos los enemigos |
| `GIT_CLONE` | Invoca un clon que ataca por ti |

> Todas las habilidades tienen un cooldown de **20 segundos**.

---

#### 🏥 Health — `/api/health` (público)

| Método | Endpoint | Descripción |
|---|---|---|
| `GET` | `/api/health` | Comprueba que la API está activa |

---

### 🧪 Testing

El proyecto incluye tests unitarios, de integración, de endpoints y de base de datos.

#### Ejecutar todos los tests

```bash
./mvnw test
```

#### Ejecutar una clase concreta

```bash
./mvnw test -Dtest=PlayerServiceImplTest
```

#### Cobertura por capas

| Capa | Herramienta | Clases testadas |
|---|---|---|
| Servicios | JUnit 5 + Mockito | `PlayerService`, `AuthService`, `UserService` |
| Seguridad | JUnit 5 + Mockito | `JwtService`, `UserDetailsConfig` |
| Controladores | MockMvc + `@WebMvcTest` | `PlayerController`, `AuthController`, `UserController` |
| Repositorios | `@DataJpaTest` + H2 | `PlayerRepository`, `UserRepository` |
| Configuración | MockMvc | `SecurityConfig` |
| Mapeo | `@SpringBootTest` | `PlayerMapper` |

---

### 👩‍💻 Autora

**Jennifer Cros** — Proyecto individual desarrollado durante el bootcamp de **FemCoders**.

**Enlace al Frontend** https://github.com/FemcodeFighters/Front-FemCodeFighters


