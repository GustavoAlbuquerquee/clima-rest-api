package br.com.pucminas.clima.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

class DirecaoVentoTest {
    @DisplayName("Converte graus para o ponto correto da rosa dos ventos")
    @ParameterizedTest(name = "{0} graus -> {1}")
    @CsvSource({
            "0,   N",
            "45,  NE",
            "90,  L",
            "106, ESE",
            "135, SE",
            "180, S",
            "225, SO",
            "270, O",
            "315, NO",
            "350, N",
            "360, N"
    })
    void deveConverterGrausEmPontoCardeal(int graus, String siglaEsperada) {
        assertThat(DirecaoVento.porGraus(graus).getSigla()).isEqualTo(siglaEsperada);
    }

    @Test
    @DisplayName("Normaliza valores fora do intervalo de 0 a 360 graus")
    void deveNormalizarGrausForaDoIntervalo() {
        assertThat(DirecaoVento.porGraus(450).getSigla()).isEqualTo("L");
        assertThat(DirecaoVento.porGraus(-90).getSigla()).isEqualTo("O");
    }

    @Test
    @DisplayName("Retorna nulo quando a API externa nao informa a direcao")
    void deveRetornarNuloQuandoDirecaoAusente() {
        assertThat(DirecaoVento.porGraus(null)).isNull();
    }
}
