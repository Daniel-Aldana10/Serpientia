# Serpentia - Juego Multijugador

## Descripción
Serpentia es un juego multijugador de serpientes con autenticación JWT, WebSocket para comunicación en tiempo real y base de datos PostgreSQL.

API REST para el juego multijugador Serpentia desarrollado con Spring Boot.

## 🚀 Características

- **Autenticación JWT**: Sistema seguro de autenticación
  - **WebSocket**: Comunicación en tiempo real
  - **Redis**: Cache y sesiones
  - **PostgreSQL**: Base de datos principal
  - **Documentación API**: Swagger/OpenAPI 3

## 📚 Documentación de la API

### Acceso a la Documentación

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API en:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Endpoints Documentados

#### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registrar nuevo usuario
  - `POST /api/auth/login` - Iniciar sesión

#### Usuario (`/api/user`)
- `GET /api/user/profile` - Obtener perfil del usuario
  - `PATCH /api/user/profile` - Actualizar perfil del usuario

### Autenticación

La API utiliza autenticación JWT. Para endpoints protegidos:

1. Obtén un token haciendo login en `/api/auth/login`
   2. Incluye el token en el header: `Authorization: Bearer <token>`

## 🛠️ Tecnologías

- **Spring Boot 3.5.3**
  - **Java 17**
  - **Spring Security + JWT**
  - **Spring Data JPA**
  - **PostgreSQL**
  - **Redis**
  - **WebSocket**
  - **Lombok**
  - **SpringDoc OpenAPI**

## 📦 Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd serpentia
   ```

2. **Configurar variables de entorno**
   Crear archivo `.env` en la raíz del proyecto:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/serpentia
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

## 🔧 Configuración

### Base de Datos
- PostgreSQL 12+
- Crear base de datos: `serpentia`

### Redis
- Redis 6+
- Puerto por defecto: 6379

## 📖 Uso de la Documentación

### En Swagger UI:
1. Abre http://localhost:8080/swagger-ui.html
2. Explora los endpoints organizados por tags
3. Prueba los endpoints directamente desde la interfaz
4. Para endpoints protegidos, usa el botón "Authorize" para incluir tu token JWT

### Ejemplos de Uso:

#### Registrar un usuario:
```bash
curl -X POST "http://localhost:8080/api/auth/register" \
     -H "Content-Type: application/json" \
     -d '{
       "username": "player123",
       "email": "player@example.com",
       "password": "password123"
     }'
```

#### Hacer login:
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
     -H "Content-Type: application/json" \
     -d '{
       "username": "player123",
       "password": "password123"
     }'
```

## 🧪 Testing

```bash
mvn test
```

## 📝 Notas de Desarrollo

- La documentación se genera automáticamente desde las anotaciones en el código
- Usa `@Operation`, `@ApiResponse`, `@Schema` para documentar endpoints y DTOs
- Los ejemplos en la documentación son interactivos y se pueden probar directamente

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## Requisitos Previos
- Java 17
- Maven
- PostgreSQL
- Redis (opcional, para futuras funcionalidades)

## Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

## Endpoints de la API

### Autenticación

#### Registro de Usuario
```http
POST /api/auth/register
Content-Type: application/json

{
    "username": "usuario123",
    "email": "usuario@ejemplo.com",
    "password": "password123"
}
```

**Validaciones:**
- Username: 3-15 caracteres alfanuméricos
- Email: formato válido y único
- Password: mínimo 6 caracteres

#### Inicio de Sesión
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "usuario123",
    "password": "password123"
}
```

**Respuesta:**
```json
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Perfil de Usuario

#### Obtener Perfil
```http
GET /api/user/profile
Authorization: Bearer <token>
```

**Respuesta:**
```json
{
    "gamesPlayed": 10,
    "gamesWon": 5,
    "totalPoints": 1500,
    "bestScore": 200,
    "winRate": 0.5
}
```

### Endpoints de Prueba

#### Endpoint Público
```http
GET /api/test/public
```

#### Endpoint Protegido
```http
GET /api/test/protected
Authorization: Bearer <token>
```

## WebSocket

### Conexión
```javascript
const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);
```

### Destinos
- `/topic/lobby` - Actualizaciones del lobby
- `/topic/game/{gameId}` - Actualizaciones del juego
- `/app/join-game` - Unirse a un juego
- `/app/leave-game` - Salir de un juego

## Estructura del Proyecto

```
src/main/java/com/serpentia/
├── config/
│   └── WebSocketConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   └── TestController.java
├── dto/
│   ├── AuthRequest.java
│   ├── AuthResponse.java
│   ├── RegisterRequest.java
│   └── UserProfileResponse.java
├── model/
│   └── User.java
├── repository/
│   └── UserRepository.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtTokenProvider.java
│   ├── JwtUtil.java
│   └── SecurityConfig.java
├── service/
│   ├── AuthService.java
│   └── UserService.java
└── SerpentiaApplication.java
```

## Funcionalidades Implementadas

### ✅ Completadas
- [x] Registro de usuario con validaciones
- [x] Inicio de sesión con JWT
- [x] Autenticación y autorización
- [x] Perfil de usuario con estadísticas
- [x] Base de datos PostgreSQL
- [x] Encriptación de contraseñas

### 🔄 En Progreso
- [ ] Lobby de juegos
- [ ] Creación de salas
- [ ] Lógica del juego
- [ ] Comunicación en tiempo real
- [ ] Configuración de WebSocket

### 📋 Pendientes
- [ ] Power-ups
- [ ] Chat rápido
- [ ] Diferentes modos de juego
- [ ] Estadísticas avanzadas

## Próximos Pasos

1. **Implementar el Lobby**: Crear la lógica para mostrar salas disponibles
2. **Sistema de Salas**: Permitir crear y unirse a salas de juego
3. **Lógica del Juego**: Implementar el juego de serpientes multijugador
4. **WebSocket Game Events**: Comunicación en tiempo real durante el juego
5. **Frontend**: Interfaz de usuario para el juego

## Tecnologías Utilizadas

- **Backend**: Spring Boot 3.5.3
- **Seguridad**: Spring Security + JWT
- **Base de Datos**: PostgreSQL + JPA/Hibernate
- **Comunicación**: WebSocket + STOMP
- **Cache**: Redis (configurado para futuras funcionalidades)
- **Build Tool**: Maven
- **Java**: 17 