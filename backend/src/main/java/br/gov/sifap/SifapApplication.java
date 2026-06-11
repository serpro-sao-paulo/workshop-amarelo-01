package br.gov.sifap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the SIFAP modular monolith.
 *
 * <p>Each bounded context lives in its own package under {@code br.gov.sifap}
 * (beneficiario, programa, calculo, pagamento, auditoria) and communicates
 * in-process via ports and domain events.
 */
@SpringBootApplication
public class SifapApplication {

    public static void main(String[] args) {
        SpringApplication.run(SifapApplication.class, args);
    }
}
