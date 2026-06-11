package br.gov.sifap.shared.error;

/** Raised when an operation conflicts with current state (HTTP 409), e.g. duplicate CPF. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
