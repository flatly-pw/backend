package pw.react.backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import pw.react.backend.security.jwt.controllers.JwtAuthenticationController;

@ControllerAdvice(annotations = RestController.class)
public class ControllerExceptionHelper {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHelper.class);

    @ExceptionHandler(value = { InvalidFileException.class })
    public ResponseEntity<ExceptionDetails> handleNotFound(InvalidFileException ex) {
        log.error("Invalid Input Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { ResourceNotFoundException.class })
    public ResponseEntity<ExceptionDetails> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource Not Found Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { UnauthorizedException.class})
    public ResponseEntity<ExceptionDetails> handleUnauthorized(UnauthorizedException ex) {
        log.error("Unauthorized Exception: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.UNAUTHORIZED, ex.getMessage());
        exceptionDetails.setPath(ex.getPath());
        return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { AuthenticationException.class})
    public ResponseEntity<ExceptionDetails> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication Exception: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.UNAUTHORIZED, ex.getMessage());
        exceptionDetails.setPath(JwtAuthenticationController.AUTHENTICATION_PATH);
        return new ResponseEntity<>(exceptionDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = { UserValidationException.class })
    public ResponseEntity<ExceptionDetails> UserValidationException(UserValidationException ex) {
        log.error("User Validation Exception: {}", ex.getMessage());
        ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.BAD_REQUEST, ex.getMessage());
        exceptionDetails.setPath(ex.getResourcePath());
        return new ResponseEntity<>(exceptionDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { UsernameNotFoundException.class })
    public ResponseEntity<ExceptionDetails> handleBadRequest(UsernameNotFoundException ex) {
        log.error("Username Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { MissingServletRequestParameterException.class})
    public ResponseEntity<ExceptionDetails> handleMissingParameterException(MissingServletRequestParameterException ex) {
        log.error("Missing parameter Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = { HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionDetails> handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("Message not readable Exception: {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDetails(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
