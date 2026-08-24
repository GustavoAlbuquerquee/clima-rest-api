package br.com.pucminas.clima.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient openMeteoRestClient(RestClient.Builder builder, OpenMeteoProperties propriedades) {
        return builder
                .requestFactory(requestFactory(propriedades.timeoutSegundos()))
                .defaultHeader("Accept", "application/json")
                .build();
    }

    private ClientHttpRequestFactory requestFactory(int timeoutSegundos) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(timeoutSegundos).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(timeoutSegundos).toMillis());
        return factory;
    }
}
