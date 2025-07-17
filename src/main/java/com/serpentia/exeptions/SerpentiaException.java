package com.serpentia.exeptions;

import org.springframework.http.HttpStatus;

/**
 * Excepción personalizada para la lógica de negocio de Serpentia.
 * Permite definir un mensaje, un mensaje especial para el usuario y un código de estado HTTP.
 */
public class SerpentiaException extends RuntimeException {
    private final String mensajeEspecial;
    private final HttpStatus status;

    /**
     * Constructor de la excepción personalizada.
     * @param message Mensaje técnico del error
     * @param mensajeEspecial Mensaje amigable para el usuario
     * @param status Código de estado HTTP a devolver
     */
    public SerpentiaException(String message, String mensajeEspecial, HttpStatus status) {
        super(message);
        this.mensajeEspecial = mensajeEspecial;
        this.status = status;
    }

    /**
     * Obtiene el mensaje especial para el usuario.
     * @return mensaje especial amigable
     */
    public String getMensajeEspecial() {
        return mensajeEspecial;
    }

    /**
     * Obtiene el código de estado HTTP asociado al error.
     * @return código de estado HTTP
     */
    public HttpStatus getStatus() {
        return status;
    }
} 