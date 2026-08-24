package br.com.pucminas.clima.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "open-meteo")
public record OpenMeteoProperties(
        String forecastUrl,
        String geocodingUrl,
        String apiKey,
        int timeoutSegundos,
        CidadePadrao cidadePadrao) {
    public record CidadePadrao(
            String nome,
            String estado,
            String pais,
            double latitude,
            double longitude,
            String fusoHorario) {
    }

    public boolean possuiApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }
}
