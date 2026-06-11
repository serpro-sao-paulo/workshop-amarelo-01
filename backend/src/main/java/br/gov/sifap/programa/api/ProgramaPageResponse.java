package br.gov.sifap.programa.api;

import java.util.List;

/** Page wrapper for program listings. */
public record ProgramaPageResponse(
        List<ProgramaResponse> content, int page, int size, long totalElements) {}
