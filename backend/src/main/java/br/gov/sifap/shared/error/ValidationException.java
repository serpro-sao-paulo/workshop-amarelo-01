package br.gov.sifap.shared.error;

/** Raised for business validation failures mapped to HTTP 400 (e.g. REQ-005). */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
