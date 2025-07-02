# Serpentia - Juego Multijugador

##  Descripción
Serpentia es un juego multijugador de serpientes desarrollado con Spring Boot que incluye autenticación JWT, comunicación en tiempo real mediante WebSocket, y persistencia distribuida con PostgreSQL y Redis.

## 🚀 Características

- **Autenticación JWT**: Sistema seguro de autenticación
   - **WebSocket**: Comunicación en tiempo real
   - **Redis**: Cache y sesiones
   - **PostgreSQL**: Base de datos principal
   - **Documentación API**: Swagger/OpenAPI 3

##  Documentación de la API

### Acceso a la Documentación

Una vez que la aplicación esté ejecutándose, puedes acceder a la documentación interactiva de la API en:

- **Swagger UI**: http://localhost:8080/swagger-ui.html


##  Stack Tecnológico

### Backend
- **Spring Boot 3.5.3**: Framework principal
- **Java 17**: Lenguaje de programación
- **Spring Security**: Autenticación y autorización
- **Spring Data JPA**: Persistencia de datos
- **Spring WebSocket**: Comunicación en tiempo real
- **JWT (jjwt 0.11.5)**: Tokens de autenticación
- **Lombok**: Reducción de código boilerplate

### Base de Datos
- **PostgreSQL**: Base de datos principal
- **Redis**: Cache y sesiones en tiempo real

### Documentación
- **SpringDoc OpenAPI 2.3.0**: Documentación de API
- **Swagger UI**: Interfaz de documentación interactiva

## Instalación y Configuración

### Requisitos Previos
- Java 17 o superior
- Maven 3.6 o superior
- PostgreSQL 12 o superior
- Redis 6 o superior

### Configuración Rápida

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/Daniel-Aldana10/Serpientia
   cd Serpientia
   ```

2. **Configurar variables de entorno**
   Crear archivo `.env` en la raíz del proyecto:
   ```env
   DB_URL=jdbc:postgresql://localhost:5432/serpentia
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   REDIS_ADDR=localhost
   REDIS_PORT=6379
   REDIS_USERNAME=default
   REDIS_PASSWORD=your_redis_password
   JWT_SECRET=your_jwt_secret_key_here
   ```
3 **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

## 🔌 Endpoints 

### Autenticación (`/api/auth`)
- `POST /api/auth/register` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión

### Usuario (`/api/user`)
- `GET /api/user/profile` - Obtener perfil del usuario
- `PATCH /api/user/profile` - Actualizar perfil del usuario

### Salas (`/api/lobby`)
- `GET /api/lobby/rooms` - Obtener todas las salas
- `POST /api/lobby/rooms` - Crear nueva sala
- `POST /api/lobby/rooms/{roomId}/join` - Unirse a sala
- `DELETE /api/lobby/rooms/{roomId}/leave` - Salir de sala
- `DELETE /api/lobby/rooms/{roomId}` - Eliminar sala
- `DELETE /api/lobby/rooms` - Eliminar todas las salas

### WebSocket
- `ws://localhost:8080/ws` - Conexión WebSocket
- `/topic/lobby` - Eventos de salas en tiempo real

## 🔐 Autenticación

La API utiliza autenticación JWT. Para endpoints protegidos:

1. **Obtener token**: Hacer login en `/api/auth/login`
2. **Incluir token**: Header `Authorization: Bearer <token>`

### Ejemplo de uso:
```bash
# Login
curl -X POST "http://localhost:8080/api/auth/login" \
     -H "Content-Type: application/json" \
     -d '{"username": "player123", "password": "password123"}'

# Usar token
curl -X GET "http://localhost:8080/api/user/profile" \
     -H "Authorization: Bearer <token>"
```

## 🎮 Modelos de Datos

### User (Entidad)
```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String username;        // Único, 3-15 caracteres alfanuméricos
    private String email;           // Único, formato válido
    private String password;        // Encriptado con BCrypt
    private Integer gamesPlayed;    // Partidas jugadas
    private Integer gamesWon;       // Partidas ganadas
    private Integer totalPoints;    // Puntos totales acumulados
    private Integer bigPoints;      // Mejor puntuación individual
}
```

### RoomDTO
```java
public class RoomDTO {
    private String roomId;                    // Identificador único de la sala
    private String host;                      // Usuario host de la sala
    private GameMode gameMode;                // COMPETITIVE, TEAM, COOPERATIVE
    private int maxPlayers;                   // Máximo de jugadores permitidos
    private List<String> currentPlayers;      // Lista de jugadores actuales
    private boolean isFull;                   // Indica si la sala está llena
    private boolean powerups;                 // Indica si los powerups están habilitados
}
```

### UserStatistics
```java
public class UserStatistics {
    private Integer gamesPlayed;    // Total de partidas jugadas
    private Integer gamesWon;       // Total de partidas ganadas
    private Integer totalPoints;    // Puntos totales acumulados
    private Integer bigPoints;      // Mejor puntuación individual
    private float ratioWin;         // Ratio de victorias (calculado automáticamente)
}
```

## 🔄 Flujos de Comunicación

### WebSocket Events
- **CREATED**: Nueva sala creada
- **UPDATED**: Sala actualizada (jugador unido/salido)
- **DELETED**: Sala eliminada
- **CLEARED**: Todas las salas eliminadas

### Ejemplo de evento:
```json
{
    "type": "UPDATED",
    "room": {
        "roomId": "12s9",
        "host": "player123",
        "gameMode": "COMPETITIVE",
        "maxPlayers": 4,
        "currentPlayers": ["player123", "player456"],
        "isFull": false,
        "powerups": true
    }
}
```

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## Funcionalidades Implementadas

### ✅ Completadas
- [x] Registro de usuario con validaciones
- [x] Inicio de sesión con JWT
- [x] Autenticación y autorización
- [x] Base de datos PostgreSQL
- [x] Encriptación de contraseñas
- [x] Comunicación en tiempo real
- [x] Configuración de WebSocket
- [x] Lobby de juegos
- [x] Creación de salas
### 🔄 En Progreso

- [ ] Lógica del juego
- [ ] Diferentes modos de juego

### 📋 Pendientes
- [ ] Power-ups
- [ ] Chat rápido
- [ ] Perfil de usuario con estadísticas


