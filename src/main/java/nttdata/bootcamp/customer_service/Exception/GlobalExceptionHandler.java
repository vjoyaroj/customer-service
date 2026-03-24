package nttdata.bootcamp.customer_service.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps runtime failures to JSON error responses (e.g. not-found style errors).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Generic handler for runtime errors used by this service.
     *
     * @param ex thrown exception
     * @return HTTP 404 with message body
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}
