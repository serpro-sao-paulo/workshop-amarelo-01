package br.gov.sifap.shared;

/** Raised when a CPF fails modulo-11 validation (REQ-001/002/003). */
public class InvalidCpfException extends RuntimeException {

    public InvalidCpfException() {
        super("CPF inválido");
    }
}
