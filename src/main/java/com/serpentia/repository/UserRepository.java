package com.serpentia.repository;

import com.serpentia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para el acceso a datos de usuarios.
 * Esta interfaz extiende {@link JpaRepository} para proporcionar operaciones
 * CRUD básicas y métodos personalizados para buscar usuarios por diferentes
 * criterios.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar
     * @return Optional que contiene el usuario si se encuentra, o vacío si no existe
     */
    Optional<User> findByUsername(String username);
}
