package com.finx.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI finxOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Finx Dashboard API")
                        .description("Personal Finance Management REST API — manages transactions, cards, stocks, and crypto assets.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Finx Team")
                                .email("dev@finx.io"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
