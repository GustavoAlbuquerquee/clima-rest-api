package br.com.pucminas.clima.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CondicaoClimaticaTest {
    @Test
    @DisplayName("Traduz o codigo WMO para a condicao correspondente")
    void deveTraduzirCodigoWmo() {
        assertThat(CondicaoClimatica.porCodigo(0)).isEqualTo(CondicaoClimatica.CEU_LIMPO);
        assertThat(CondicaoClimatica.porCodigo(61).getDescricao()).isEqualTo("Chuva fraca");
        assertThat(CondicaoClimatica.porCodigo(95).getDescricao()).isEqualTo("Trovoada");
    }

    @Test
    @DisplayName("Codigo desconhecido nao quebra a resposta")
    void deveUsarCondicaoDesconhecidaParaCodigoNaoMapeado() {
        assertThat(CondicaoClimatica.porCodigo(999)).isEqualTo(CondicaoClimatica.DESCONHECIDA);
    }

    @Test
    @DisplayName("Codigo ausente e tratado como condicao desconhecida")
    void deveUsarCondicaoDesconhecidaParaCodigoNulo() {
        assertThat(CondicaoClimatica.porCodigo(null)).isEqualTo(CondicaoClimatica.DESCONHECIDA);
    }
}
