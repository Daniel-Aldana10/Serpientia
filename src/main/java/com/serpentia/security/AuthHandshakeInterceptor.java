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

        // Extraer token
        String token = null;
        if (uri.contains("token=")) {
            int tokenStart = uri.indexOf("token=") + 6;
            int tokenEnd = uri.indexOf('&', tokenStart);
            if (tokenEnd == -1) {
                token = uri.substring(tokenStart);
            } else {
                token = uri.substring(tokenStart, tokenEnd);
            }
        }

        // Extraer roomId
        String roomId = null;
        if (uri.contains("roomId=")) {
            int roomStart = uri.indexOf("roomId=") + 7;
            int roomEnd = uri.indexOf('&', roomStart);
            if (roomEnd == -1) {
                roomId = uri.substring(roomStart);
            } else {
                roomId = uri.substring(roomStart, roomEnd);
            }
        }

        if (token != null) {
            try {
                String username = jwtUtil.extractUserName(token);
                attributes.put("username", username);
                if (roomId != null) {
                    attributes.put("roomId", roomId);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }


        return false;
    }
    /*
     * No es necesario de realizar
     */
    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }
}
