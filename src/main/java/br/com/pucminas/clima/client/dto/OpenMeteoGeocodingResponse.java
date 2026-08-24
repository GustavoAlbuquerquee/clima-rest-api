package br.com.pucminas.clima.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoGeocodingResponse(List<Resultado> results) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Resultado(
            String name,
            Double latitude,
            Double longitude,
            Double elevation,
            String timezone,
            String country,

            String admin1) {
    }

    public boolean vazia() {
        return results == null || results.isEmpty();
    }
}
