# 🐍 Serpentia - Juego de Serpientes Multiplayer

Serpentia es un juego de serpientes multiplayer desarrollado en Spring Boot con WebSocket para comunicación en tiempo real. Los jugadores pueden competir individualmente o en equipos de 2 jugadores.

## 🎮 Características

- **Modo Competitivo**: Todos contra todos
- **Modo por Equipos**: 4 jugadores, 2 equipos de 2 jugadores
- **Comunicación en tiempo real** via WebSocket
- **Sistema de puntuación** y estadísticas
- **Autenticación JWT**
- **Persistencia en Redis**
- **API REST** completa

## 🚀 Instalación

### Prerrequisitos

- Java17 o superior
- Maven 3.6+
- Redis Server

### Configuración

1. **Clonar el repositorio**
```bash
git clone <https://github.com/Daniel-Aldana10/Serpientia>
cd Serpientia
```

2. **Configurar Redis**
```bash
# O usar Docker
docker run -d -p 6379:6379redis:latest
```

3. **Configurar variables de entorno**
```bash
# Crear archivo .env o configurar variables de entorno
export JWT_SECRET=tu_clave_secreta_muy_segura
export REDIS_HOST=localhost
export REDIS_PORT=6379```

4. **Compilar y ejecutar**
```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080
# API Endpoints

### Autenticación

#### Registro
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "player123 email: player@example.com,
  ssword": "password123"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "player123,
  ssword": "password123}
```

**Respuesta:**
```json[object Object]
  token:eyJhbGciOiJIUzI1NiIsInR5CI6IkpXVCJ9}
```

### Lobby

#### Obtener salas
```http
GET /api/lobby/rooms
Authorization: Bearer <token>
```

#### Crear sala
```http
POST /api/lobby/rooms
Authorization: Bearer <token>
Content-Type: application/json

[object Object]roomId":abc123,
 gameMode": "TEAM,
  maxPlayers": 4,
 powerups": true
}
```

#### Unirse a sala
```http
POST /api/lobby/rooms/{roomId}/join
Authorization: Bearer <token>
```

#### Salir de sala
```http
DELETE /api/lobby/rooms/{roomId}/leave
Authorization: Bearer <token>
```

### Juego

#### Iniciar juego
```http
POST /api/game/start/{roomId}
Authorization: Bearer <token>
```

#### Obtener estado del juego
```http
GET /api/game/state/{roomId}
Authorization: Bearer <token>
```

### Usuario

#### Obtener perfil
```http
GET /api/user/profile
Authorization: Bearer <token>
```

## 🎯 Modos de Juego

### Modo Competitivo
- **Jugadores**: 2-4 Jugadores
- **Objetivo**: Ser el último jugador vivo o llegar a la meta de puntos
- **Eliminación**: Individual
- **Ganador**: Último jugador con vida o llegar a la meta de puntos

### Modo por Equipos
- **Jugadores**: Exactamente 4 jugadores
- **Equipos**: De 2 jugadores
- **Asignación**: Automática por orden de llegada
  - Jugadores 01→ Equipo 1 (Rojo)
  - Jugadores 23→ Equipo 2Azul)
- **Objetivo**: Ser el equipo con al menos un jugador vivo
- **Eliminación**: Individual (no elimina todo el equipo)


## 🏗️ Arquitectura

### Componentes principales
- **Controllers**: Manejan requests HTTP
- **Services**: Lógica de negocio
- **Repositories**: Acceso a datos (Redis)
- **WebSocket**: Comunicación en tiempo real
- **Security**: Autenticación JWT
- **Events**: Sistema de eventos para estadísticas


## 🤝 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -mAdd some AmazingFeature'`)4 Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.txt](LICENSE.txt) para detalles.
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
- [x] Lógica del juego
- [x] Perfil de usuario con estadísticas
### 🔄 En Progreso
- [ ] Diferentes modos de juego
- [ ] Power-ups





