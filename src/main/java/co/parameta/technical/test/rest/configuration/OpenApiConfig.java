package co.parameta.technical.test.rest.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration for the REST API.
 * <p>
 * This configuration defines the global API metadata and
 * sets up JWT Bearer authentication to be used across all
 * secured endpoints.
 * </p>
 *
 * <ul>
 *     <li>API title, version and description</li>
 *     <li>JWT Bearer authentication scheme</li>
 *     <li>Global security requirement</li>
 * </ul>
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Parameta Technical Test - REST API",
                version = "v1",
                description = "REST endpoints for employee operations"
        )
)
public class OpenApiConfig {

    /**
     * Creates and configures the {@link OpenAPI} bean used by Swagger UI.
     * <p>
     * This method registers a HTTP Bearer security scheme with JWT format
     * and applies it as a global security requirement, so all endpoints
     * will require an Authorization header unless explicitly overridden.
     * </p>
     *
     * @return configured {@link OpenAPI} instance
     */
    @Bean
    public OpenAPI openAPI() {
        String schemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(
                        new Components().addSecuritySchemes(
                                schemeName,
                                new SecurityScheme()
                                        .name(schemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
