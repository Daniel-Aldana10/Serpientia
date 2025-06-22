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

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }

    public void registerUser(RegisterRequest request) {
        // Validar que el username no exista
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }

        // Validar que el email no exista
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar longitud del username (3-15 caracteres)
        if (request.getUsername().length() < 3 || request.getUsername().length() > 15) {
            throw new RuntimeException("El nombre de usuario debe tener entre 3 y 15 caracteres");
        }

        // Validar que el username sea alfanumérico
        if (!request.getUsername().matches("^[a-zA-Z0-9]+$")) {
            throw new RuntimeException("El nombre de usuario solo puede contener letras y números");
        }

        User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public void updateUserStats(String username, int points, boolean won) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

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
    public UserStatistics getStatsUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        return new UserStatistics(user.getGamesPlayed(), user.getGamesWon(), user.getTotalPoints(), user.getGamesWon(), (float) user.getGamesPlayed() /user.getGamesWon());
    }
} 