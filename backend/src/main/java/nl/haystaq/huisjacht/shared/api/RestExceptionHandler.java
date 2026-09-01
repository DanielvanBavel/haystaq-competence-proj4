package nl.haystaq.huisjacht.shared.api;

import nl.haystaq.huisjacht.shared.domain.BusinessRuleViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(BusinessRuleViolation.class)
    public ResponseEntity<Map<String, String>> handleDomain(BusinessRuleViolation exception) {
        HttpStatus status = switch (exception.kind()) {
            case INVALID_INPUT -> HttpStatus.BAD_REQUEST;
            case CONFLICT -> HttpStatus.CONFLICT;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
        };
        return ResponseEntity.status(status)
                .body(Map.of("code", exception.code(), "message", exception.getMessage()));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception exception) {
        return ResponseEntity.badRequest()
                .body(Map.of("code", "request.invalid", "message", "Het verzoek kon niet gelezen worden."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception exception) {
        String reference = UUID.randomUUID().toString().substring(0, 8);
        log.error("onverwachte fout ref={}", reference, exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("code", "internal_error", "message", "Er ging iets mis.", "ref", reference));
    }
}
