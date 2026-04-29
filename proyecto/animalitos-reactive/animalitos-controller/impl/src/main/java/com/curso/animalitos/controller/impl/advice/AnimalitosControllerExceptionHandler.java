package com.curso.animalitos.controller.impl.advice;

import com.curso.animalitos.common.dto.ValidationErrorDTO;
import com.curso.animalitos.controller.api.models.ErrorResponseDTO;
import com.curso.animalitos.service.api.exceptions.AnimalNoEncontradoException;
import com.curso.animalitos.service.api.exceptions.AnimalYaExisteException;
import com.curso.animalitos.service.api.exceptions.AnimalitosServiceException;
import com.curso.animalitos.service.api.exceptions.DatosInvalidosException;
import com.curso.animalitos.service.api.exceptions.RepositorioInaccesibleException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;

/**
 * AOP via @RestControllerAdvice (dia3): centraliza el mapeo de excepciones
 * de negocio a HTTP. Compatible con WebFlux: las excepciones que viajan en
 * el flujo reactivo (Mono.error / Flux.error) se interceptan aqui igual
 * que en Spring MVC.
 *
 * Nota: en WebFlux las violaciones de @Valid en @RequestBody no llegan como
 * MethodArgumentNotValidException sino como {@link WebExchangeBindException}.
 */
@RestControllerAdvice
public class AnimalitosControllerExceptionHandler {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(AnimalitosControllerExceptionHandler.class);

    @ExceptionHandler(AnimalNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoEncontrado(AnimalNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponseDTO.of(404, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(AnimalYaExisteException.class)
    public ResponseEntity<ErrorResponseDTO> handleYaExiste(AnimalYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponseDTO.of(409, "Conflict", ex.getMessage()));
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<ErrorResponseDTO> handleDatosInvalidos(DatosInvalidosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(RepositorioInaccesibleException.class)
    public ResponseEntity<ErrorResponseDTO> handleRepoInaccesible(RepositorioInaccesibleException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponseDTO.of(503, "Service Unavailable", ex.getMessage()));
    }

    @ExceptionHandler(AnimalitosServiceException.class)
    public ResponseEntity<ErrorResponseDTO> handleServiceGeneric(AnimalitosServiceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of(500, "Internal Server Error", ex.getMessage()));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(WebExchangeBindException ex) {
        List<ValidationErrorDTO> detalles = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> ValidationErrorDTO.of(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of(400, "Bad Request", "Datos invalidos", detalles));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAny(Exception ex) {
        log.error("Excepcion no controlada", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of(500, "Internal Server Error", ex.getMessage()));
    }
}
