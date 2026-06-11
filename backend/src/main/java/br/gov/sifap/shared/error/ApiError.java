package br.gov.sifap.shared.error;

import java.util.List;

/** Error payload matching the {@code Erro} schema in contracts/openapi.yaml. */
public record ApiError(String codigo, String mensagem, List<String> detalhes) {

    public static ApiError of(String codigo, String mensagem) {
        return new ApiError(codigo, mensagem, List.of());
    }
}
