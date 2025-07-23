package com.serpentia.service;

import com.serpentia.dto.RegisterRequest;
import com.serpentia.dto.UserStatistics;
import com.serpentia.model.User;
import com.serpentia.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.serpentia.exeptions.SerpentiaException;
/**
 * Servicio que maneja toda la lógica de negocio relacionada con los usuarios.
 * 
 * <p>Esta clase implementa {@link UserDetailsService} para integrarse con Spring Security
 * y proporciona métodos para el registro, autenticación y gestión de estadísticas de usuarios.</p>
 * 
 * <p>El servicio incluye validaciones de negocio para asegurar la integridad de los datos
 * y la unicidad de los usuarios en el sistema.</p>
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor que inyecta las dependencias necesarias.
     * 
     * @param userRepository Repositorio para acceso a datos de usuarios
     * @param passwordEncoder Codificador de contraseñas para encriptación
     */
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * Este método es requerido por Spring Security para la autenticación.
     * Convierte la entidad User en un UserDetails de Spring Security.
     * 
     * @param username Nombre de usuario a buscar
     * @return UserDetails del usuario encontrado
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SerpentiaException("Usuario no encontrado: " + username, "No se encontró usuario para esas credenciales.", org.springframework.http.HttpStatus.NOT_FOUND));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * @param request Datos de registro del usuario
     * @throws RuntimeException si las validaciones fallan
     */
    public void registerUser(RegisterRequest request) {
        // Validar que el username no exista
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new SerpentiaException("El nombre de usuario ya está en uso", "Verifica los datos enviados, algo no está bien.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        // Validar longitud del username (3-15 caracteres)
        if (request.getUsername().length() < 3 || request.getUsername().length() > 15) {
            throw new SerpentiaException("El nombre de usuario debe tener entre 3 y 15 caracteres", "Verifica los datos enviados, algo no está bien.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        // Validar que el username sea alfanumérico
        if (!request.getUsername().matches("^[a-zA-Z0-9]+$")) {
            throw new SerpentiaException("El nombre de usuario solo puede contener letras y números", "Verifica los datos enviados, algo no está bien.", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /**
     * Actualiza las estadísticas de un usuario después de una partida.
     *
     * @param username Nombre de usuario
     * @param points Puntos obtenidos en la partida
     * @param won Indica si el usuario ganó la partida
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public void updateUserStats(String username, int points, boolean won) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SerpentiaException("Usuario no encontrado: " + username, "No se encontró usuario para esas credenciales.", org.springframework.http.HttpStatus.NOT_FOUND));

        user.setGamesPlayed(user.getGamesPlayed() + 1);
        user.setTotalPoints(user.getTotalPoints() + points);
        
        if (won) {
            user.setGamesWon(user.getGamesWon() + 1);
        }
        
        if (points > user.getBigPoints()) {
            user.setBigPoints(points);
        }

        userRepository.save(user);
    }

    /**
     * Obtiene las estadísticas completas de un usuario.
     *
     * @param username Nombre de usuario
     * @return Estadísticas del usuario incluyendo ratio de victorias
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public UserStatistics getStatsUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new SerpentiaException("Usuario no encontrado: " + username, "No se encontró usuario para esas credenciales.", org.springframework.http.HttpStatus.NOT_FOUND));
        
        // Calcular ratio de victorias (evitar división por cero)
        float ratioWin = user.getGamesPlayed() > 0 ? 
            (float) user.getGamesWon() / user.getGamesPlayed() * 100 : 0.0f;
        
        return new UserStatistics(
            user.getGamesPlayed(), 
            user.getGamesWon(), 
            user.getTotalPoints(), 
            user.getBigPoints(), 
            ratioWin
        );
    }
} 