package br.com.pucminas.clima.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI climaOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API REST de Clima - Belo Horizonte/MG")
                .description("""
                        API REST desenvolvida com Spring Boot que consome a API externa Open-Meteo,
                        processa a resposta e disponibiliza as informações meteorológicas de
                        Belo Horizonte - MG (e de outras cidades) em um formato próprio da aplicação.
                        """)
                .version("1.0.0")
                .contact(new Contact().name("Desenvolvimento e Integração de Aplicações Web - PUC Minas"))
                .license(new License().name("MIT")));
    }
}
