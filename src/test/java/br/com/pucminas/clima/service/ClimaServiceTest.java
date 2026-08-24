package br.com.pucminas.clima.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.pucminas.clima.client.OpenMeteoClient;
import br.com.pucminas.clima.client.dto.OpenMeteoForecastResponse;
import br.com.pucminas.clima.client.dto.OpenMeteoGeocodingResponse;
import br.com.pucminas.clima.config.OpenMeteoProperties;
import br.com.pucminas.clima.dto.ClimaResponse;
import br.com.pucminas.clima.exception.CidadeNaoEncontradaException;

@ExtendWith(MockitoExtension.class)
class ClimaServiceTest {
    @Mock
    private OpenMeteoClient client;

    private ClimaService service;

    @BeforeEach
    void configurar() {
        OpenMeteoProperties propriedades = new OpenMeteoProperties(
                "https://api.open-meteo.com/v1/forecast",
                "https://geocoding-api.open-meteo.com/v1/search",
                "",
                10,
                new OpenMeteoProperties.CidadePadrao(
                        "Belo Horizonte", "Minas Gerais", "Brasil",
                        -19.92083, -43.93778, "America/Sao_Paulo"));

        service = new ClimaService(client, propriedades);
    }

    @Test
    @DisplayName("Converte a resposta da API externa no objeto proprio da aplicacao")
    void deveConverterRespostaDaApiExterna() {
        when(client.buscarPrevisao(anyDouble(), anyDouble(), anyString(), anyInt()))
                .thenReturn(respostaDeExemplo());

        ClimaResponse resposta = service.consultarCidadePadrao(3);

        assertThat(resposta.localizacao().cidade()).isEqualTo("Belo Horizonte");
        assertThat(resposta.localizacao().estado()).isEqualTo("Minas Gerais");
        assertThat(resposta.fonte()).isEqualTo("Open-Meteo");

        assertThat(resposta.atual().temperatura()).isEqualTo(17.5);
        assertThat(resposta.atual().umidade()).isEqualTo(81);
        assertThat(resposta.atual().ventoVelocidade()).isEqualTo(14.8);

        assertThat(resposta.atual().ventoDirecao()).isEqualTo("ESE");
        assertThat(resposta.atual().ventoDirecaoExtenso()).isEqualTo("Lés-sudeste");

        assertThat(resposta.atual().descricao()).isEqualTo("Predominantemente limpo");
        assertThat(resposta.atual().diurno()).isTrue();

        assertThat(resposta.atual().temperaturaMaxima()).isEqualTo(26.7);
        assertThat(resposta.atual().temperaturaMinima()).isEqualTo(16.3);
    }

    @Test
    @DisplayName("Pivota as listas paralelas do bloco diario em uma previsao por dia")
    void deveConverterPrevisaoDiaria() {
        when(client.buscarPrevisao(anyDouble(), anyDouble(), anyString(), anyInt()))
                .thenReturn(respostaDeExemplo());

        ClimaResponse resposta = service.consultarCidadePadrao(3);

        assertThat(resposta.previsao()).hasSize(3);
        assertThat(resposta.previsao().get(0).data()).isEqualTo(LocalDate.of(2026, 8, 24));
        assertThat(resposta.previsao().get(2).temperaturaMaxima()).isEqualTo(29.6);
        assertThat(resposta.previsao().get(1).descricao()).isEqualTo("Garoa fraca");
    }

    @Test
    @DisplayName("Lanca excecao quando a geocodificacao nao encontra a cidade")
    void deveLancarExcecaoQuandoCidadeNaoEncontrada() {
        when(client.geocodificar(anyString())).thenReturn(new OpenMeteoGeocodingResponse(List.of()));

        assertThatThrownBy(() -> service.consultarPorCidade("cidade-inexistente", 3))
                .isInstanceOf(CidadeNaoEncontradaException.class)
                .hasMessageContaining("cidade-inexistente");
    }

    @Test
    @DisplayName("Ausencia do bloco diario nao quebra a conversao")
    void deveTolerarBlocoDiarioAusente() {
        OpenMeteoForecastResponse semDiario = new OpenMeteoForecastResponse(
                -19.93, -43.97, 843.0, "America/Sao_Paulo", -10800,
                respostaDeExemplo().current(), null);

        when(client.buscarPrevisao(anyDouble(), anyDouble(), anyString(), anyInt())).thenReturn(semDiario);

        ClimaResponse resposta = service.consultarCidadePadrao(3);

        assertThat(resposta.previsao()).isEmpty();
        assertThat(resposta.atual().temperaturaMaxima()).isNull();
        assertThat(resposta.atual().temperatura()).isEqualTo(17.5);
    }

    private OpenMeteoForecastResponse respostaDeExemplo() {
        OpenMeteoForecastResponse.Current atual = new OpenMeteoForecastResponse.Current(
                "2026-08-24T07:30", 17.5, 16.7, 81, 925.9, 0.0, 1, 14.8, 106, 1);

        OpenMeteoForecastResponse.Daily diario = new OpenMeteoForecastResponse.Daily(
                List.of("2026-08-24", "2026-08-25", "2026-08-26"),
                List.of(80, 51, 81),
                List.of(26.7, 26.3, 29.6),
                List.of(16.3, 16.1, 17.7),
                List.of(2.1, 0.4, 5.6),
                List.of(65, 30, 80),
                List.of(22.4, 18.1, 25.0),
                List.of("2026-08-24T06:32", "2026-08-25T06:31", "2026-08-26T06:30"),
                List.of("2026-08-24T17:48", "2026-08-25T17:48", "2026-08-26T17:49"));

        return new OpenMeteoForecastResponse(
                -19.9297, -43.966034, 843.0, "America/Sao_Paulo", -10800, atual, diario);
    }
}
