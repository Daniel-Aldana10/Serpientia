package com.serpentia.config;

import com.serpentia.security.AuthHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para comunicación en tiempo real.
 * 
 * Esta clase configura STOMP (Simple Text Oriented Messaging Protocol) sobre WebSocket
 * para permitir la comunicación en tiempo real entre el servidor y los clientes.
 * La configuración incluye endpoints, broker de mensajes e interceptores de autenticación.</p>
 *
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    /**
     * Constructor que inyecta el interceptor de autenticación.
     * 
     * @param authHandshakeInterceptor Interceptor para validar tokens JWT en conexiones WebSocket
     */
    public WebSocketConfig(AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    /**
     * Registra los endpoints de WebSocket.
     * 
     * <Configura el endpoint principal "/ws" con las siguientes características:
     *
     * Soporte para SockJS para compatibilidad con navegadores
     * Interceptor de autenticación para validar tokens JWT
     * CORS configurado para permitir conexiones desde el frontend
     *
     * 
     * @param registry Registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("http://localhost:5173")
                .withSockJS(); // Soporte para SockJS
    }

    /**
     * Configura el broker de mensajes.
     * Configura un broker simple que permite:
     * Enviar mensajes a topics con el prefijo "/topic"
     * Recibir mensajes de aplicaciones con el prefijo "/app"<
     * 
     * @param registry Registro de configuración del broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Habilitar broker simple para topics
        registry.enableSimpleBroker("/topic");
        
        // Configurar prefijo para mensajes de aplicación
        registry.setApplicationDestinationPrefixes("/app");
    }
}
