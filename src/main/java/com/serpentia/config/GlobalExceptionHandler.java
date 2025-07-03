package com.serpentia.config;

import com.serpentia.exeptions.SerpentiaException;
import com.serpentia.model.dto.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

/**
 * Manejador global de excepciones para la API de Serpentia.
 * Captura y transforma las excepciones lanzadas en los controladores y servicios,
 * devolviendo respuestas JSON estructuradas y códigos HTTP apropiados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones personalizadas de Serpentia.
     * @param ex la excepción personalizada lanzada en la lógica de negocio
     * @return respuesta con detalles del error y el código HTTP correspondiente
     */
    @ExceptionHandler(SerpentiaException.class)
    public ResponseEntity<ErrorDetails> handleSerpentiaException(SerpentiaException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .mensajeEspecial(ex.getMensajeEspecial())
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, ex.getStatus());
    }

    /**
     * Maneja excepciones estándar de Spring con código de estado.
     * @param ex excepción de tipo ResponseStatusException
     * @return respuesta con detalles del error y el código HTTP correspondiente
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorDetails> handleResponseStatusException(ResponseStatusException ex) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getReason())
                .mensajeEspecial("Ha ocurrido un error con la solicitud.")
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, ex.getStatusCode());
    }

    /**
     * Maneja cualquier otra excepción no controlada.
     * @param ex excepción genérica
     * @return respuesta con detalles del error y código 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetails> handleGeneral(Exception ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui") || uri.equals("/swagger-ui.html")) {
            // Deja que Spring maneje estas rutas
            return null;
        }
        ErrorDetails errorDetails = ErrorDetails.builder()
                .message(ex.getMessage())
                .mensajeEspecial("Ha ocurrido un error inesperado. Intenta más tarde.")
                .date(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 