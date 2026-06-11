package br.gov.sifap.shared.error;

/** Raised when a requested resource does not exist (HTTP 404). */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
