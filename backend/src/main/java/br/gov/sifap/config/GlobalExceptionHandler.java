package br.gov.sifap.config;

import br.gov.sifap.shared.InvalidCpfException;
import br.gov.sifap.shared.error.ApiError;
import br.gov.sifap.shared.error.ConflictException;
import br.gov.sifap.shared.error.NotFoundException;
import br.gov.sifap.shared.error.ValidationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps domain and validation exceptions to the {@code Erro} contract. */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest()
                .body(new ApiError("VALIDACAO", "Erro de validação", detalhes));
    }

    @ExceptionHandler(InvalidCpfException.class)
    public ResponseEntity<ApiError> handleInvalidCpf(InvalidCpfException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("CPF_INVALIDO", ex.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleBusinessValidation(ValidationException ex) {
        return ResponseEntity.badRequest().body(ApiError.of("VALIDACAO", ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("CONFLITO", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of("NAO_ENCONTRADO", ex.getMessage()));
    }
}
