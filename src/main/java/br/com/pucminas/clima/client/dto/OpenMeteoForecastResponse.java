package br.com.pucminas.clima.client.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoForecastResponse(
        Double latitude,
        Double longitude,
        Double elevation,
        String timezone,
        @JsonProperty("utc_offset_seconds") Integer utcOffsetSeconds,
        Current current,
        Daily daily) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Current(
            String time,
            @JsonProperty("temperature_2m") Double temperatura,
            @JsonProperty("apparent_temperature") Double sensacaoTermica,
            @JsonProperty("relative_humidity_2m") Integer umidade,
            @JsonProperty("surface_pressure") Double pressao,
            @JsonProperty("precipitation") Double precipitacao,
            @JsonProperty("weather_code") Integer codigoCondicao,
            @JsonProperty("wind_speed_10m") Double ventoVelocidade,
            @JsonProperty("wind_direction_10m") Integer ventoDirecaoGraus,
            @JsonProperty("is_day") Integer diurno) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Daily(
            List<String> time,
            @JsonProperty("weather_code") List<Integer> codigoCondicao,
            @JsonProperty("temperature_2m_max") List<Double> temperaturaMaxima,
            @JsonProperty("temperature_2m_min") List<Double> temperaturaMinima,
            @JsonProperty("precipitation_sum") List<Double> precipitacaoTotal,
            @JsonProperty("precipitation_probability_max") List<Integer> probabilidadePrecipitacao,
            @JsonProperty("wind_speed_10m_max") List<Double> ventoVelocidadeMaxima,
            @JsonProperty("sunrise") List<String> nascerDoSol,
            @JsonProperty("sunset") List<String> porDoSol) {
    }
}
