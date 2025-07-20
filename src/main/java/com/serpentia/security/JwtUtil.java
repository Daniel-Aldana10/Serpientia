package com.serpentia.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidades para el manejo de tokens JWT (JSON Web Tokens).
 * Esta clase proporciona métodos para generar, validar y extraer información
 * de tokens JWT utilizados para la autenticación en el sistema
 */
@Component
public class JwtUtil {
    
    /**
     * Clave secreta para firmar y verificar tokens JWT.
     * Se configura a través de la variable de entorno JWT_SECRET.
     */
    @Value("${JWT_SECRET}")
    private String secret;

    /**
     * Genera un token JWT para un usuario autenticado.
     * 
     * @param authentication Objeto de autenticación de Spring Security
     * @return Token JWT generado como String
     * @throws IllegalArgumentException si la autenticación es null o inválida
     */
    public String generateToken(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Autenticación inválida");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .signWith(key)
                .compact();
    }

    /**
     * Valida un token JWT contra los detalles de un usuario.
     *
     * @param token Token JWT a validar
     * @param userDetails Detalles del usuario contra el cual validar
     * @return true si el token es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = extractUserName(token);
            return (userName.equals(userDetails.getUsername()));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae todos los claims (reclamaciones) de un token JWT.
     * 
     * <p>Este método parsea el token y extrae toda la información contenida
     * en el payload del JWT.</p>
     * 
     * @param token Token JWT del cual extraer los claims
     * @return Claims extraídos del token
     * @throws io.jsonwebtoken.JwtException si el token es inválido o está malformado
     */
    public Claims extractAllClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     * Este método es una conveniencia que extrae específicamente el subject
     * del token, que contiene el nombre de usuario.
     * 
     * @param token Token JWT del cual extraer el nombre de usuario
     * @return Nombre de usuario extraído del token
     * @throws io.jsonwebtoken.JwtException si el token es inválido
     */
    public String extractUserName(String token) {
        return extractAllClaims(token).getSubject();
    }
}
