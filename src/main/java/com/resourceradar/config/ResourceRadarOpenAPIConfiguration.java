package com.resourceradar.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info =
        @Info(
                title = "Resource Radar API v1",
                description = "This API offers a wide range of project management features, including the ability to add employees, create new projects, and allocate resources",
                version = "1.2.0.5",
                contact = @Contact(name = "Fission Labs", email = "info@fissionlabs.com")),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "bearerAuth", scheme = "Bearer")
public class ResourceRadarOpenAPIConfiguration {

    @Bean
    public OperationCustomizer customize() {
        return (operation, handlerMethod) -> {
            operation.getParameters().removeIf(parameter -> "X-Org-Id".equals(parameter.getName()));
            operation.addParametersItem(
                    new Parameter().name("X-Org-Id")
                            .schema(new StringSchema())
                            .description("Organization Id")
                            .in(ParameterIn.HEADER.toString())
                            .required(true));
            return operation;
        };
    }


    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addParameters("X-Org-Id", new Parameter()
                                .in(ParameterIn.HEADER.toString())
                                .name("X-Org-Id")
                                .schema(new StringSchema())
                                .required(true)));
    }

}
