package com.serpentia.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String uri = request.getURI().toString();

        if (uri.contains("token=")) {
            String token = uri.substring(uri.indexOf("token=") + 6);

            try {
                String username = jwtUtil.extractUserName(token);
                attributes.put("username", username);  // Guarda el username para usar después
                return true;
            } catch (Exception e) {
                System.out.println("Token inválido: " + e.getMessage());
                return false;
            }
        }

        System.out.println("No se encontró token en la URL");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // No se necesita lógica post-handshake aquí
    }
}
