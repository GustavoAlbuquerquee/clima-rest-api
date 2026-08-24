package br.com.pucminas.clima.client;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.pucminas.clima.client.dto.OpenMeteoForecastResponse;
import br.com.pucminas.clima.client.dto.OpenMeteoGeocodingResponse;
import br.com.pucminas.clima.config.OpenMeteoProperties;
import br.com.pucminas.clima.exception.ClimaIndisponivelException;
import br.com.pucminas.clima.exception.RequisicaoInvalidaException;

@Component
public class OpenMeteoClient {
    private static final Logger log = LoggerFactory.getLogger(OpenMeteoClient.class);

    private static final String VARIAVEIS_ATUAIS = String.join(",",
            "temperature_2m",
            "relative_humidity_2m",
            "apparent_temperature",
            "is_day",
            "precipitation",
            "weather_code",
            "surface_pressure",
            "wind_speed_10m",
            "wind_direction_10m");

    private static final String VARIAVEIS_DIARIAS = String.join(",",
            "weather_code",
            "temperature_2m_max",
            "temperature_2m_min",
            "precipitation_sum",
            "precipitation_probability_max",
            "wind_speed_10m_max",
            "sunrise",
            "sunset");

    private final RestClient restClient;
    private final OpenMeteoProperties propriedades;

    public OpenMeteoClient(RestClient openMeteoRestClient, OpenMeteoProperties propriedades) {
        this.restClient = openMeteoRestClient;
        this.propriedades = propriedades;
    }

    public OpenMeteoForecastResponse buscarPrevisao(double latitude, double longitude, String fusoHorario, int dias) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(propriedades.forecastUrl())
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current", VARIAVEIS_ATUAIS)
                .queryParam("daily", VARIAVEIS_DIARIAS)
                .queryParam("timezone", fusoHorario)
                .queryParam("forecast_days", dias);

        if (propriedades.possuiApiKey()) {
            uri.queryParam("apikey", propriedades.apiKey());
        }

        OpenMeteoForecastResponse resposta = executar(uri.encode().build().toUri(), OpenMeteoForecastResponse.class);
        if (resposta == null || resposta.current() == null) {
            throw new ClimaIndisponivelException(
                    "A API de clima respondeu sem os dados meteorológicos esperados.");
        }
        return resposta;
    }

    public OpenMeteoGeocodingResponse geocodificar(String cidade) {
        URI uri = UriComponentsBuilder.fromUriString(propriedades.geocodingUrl())
                .queryParam("name", cidade)
                .queryParam("count", 1)
                .queryParam("language", "pt")
                .queryParam("format", "json")
                .encode()
                .build()
                .toUri();

        OpenMeteoGeocodingResponse resposta = executar(uri, OpenMeteoGeocodingResponse.class);
        return resposta == null ? new OpenMeteoGeocodingResponse(null) : resposta;
    }

    private <T> T executar(URI uri, Class<T> tipoResposta) {
        log.debug("Chamando API externa de clima: {}", uri);
        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()

                    .onStatus(status -> status.is4xxClientError(), (req, res) -> {
                        throw new RequisicaoInvalidaException(
                                "A API de clima recusou a requisição (HTTP %d).".formatted(res.getStatusCode().value()));
                    })

                    .onStatus(status -> status.is5xxServerError(), (req, res) -> {
                        throw new ClimaIndisponivelException(
                                "A API de clima está indisponível no momento (HTTP %d)."
                                        .formatted(res.getStatusCode().value()));
                    })
                    .body(tipoResposta);
        } catch (ResourceAccessException e) {
            log.error("Falha de comunicação com a API de clima: {}", e.getMessage());
            throw new ClimaIndisponivelException(
                    "Não foi possível se comunicar com a API de clima. Verifique a conexão com a internet.", e);
        } catch (ClimaIndisponivelException | RequisicaoInvalidaException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Resposta inesperada da API de clima: {}", e.getMessage());
            throw new ClimaIndisponivelException("A API de clima retornou uma resposta inesperada.", e);
        }
    }
}
