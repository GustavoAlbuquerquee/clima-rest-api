package br.com.pucminas.clima;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ClimaRestApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(ClimaRestApiApplication.class, args);
	}
}
