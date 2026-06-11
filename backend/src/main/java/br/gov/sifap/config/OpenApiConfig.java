package br.gov.sifap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** OpenAPI/Swagger metadata for the {@code /api/v1} surface (REQ-033). */
@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI sifapOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SIFAP API")
                        .version("1.0.0")
                        .description("API REST do SIFAP modernizado (Modular Monolith)."))
                .addSecurityItem(new SecurityRequirement().addList(BEARER))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
