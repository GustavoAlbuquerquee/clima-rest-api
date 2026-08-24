package br.com.pucminas.clima.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.pucminas.clima.dto.ClimaResponse;
import br.com.pucminas.clima.dto.CondicaoAtualDto;
import br.com.pucminas.clima.dto.CondicaoAtualDto.UnidadesDto;
import br.com.pucminas.clima.dto.LocalizacaoDto;
import br.com.pucminas.clima.exception.CidadeNaoEncontradaException;
import br.com.pucminas.clima.exception.ClimaIndisponivelException;
import br.com.pucminas.clima.service.ClimaService;

@WebMvcTest(ClimaController.class)
class ClimaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClimaService climaService;

    @Test
    @DisplayName("GET /clima retorna 200 com os dados de Belo Horizonte")
    void deveRetornarClimaDeBeloHorizonte() throws Exception {
        when(climaService.consultarCidadePadrao(anyInt())).thenReturn(respostaDeExemplo());

        mockMvc.perform(get("/clima"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localizacao.cidade").value("Belo Horizonte"))
                .andExpect(jsonPath("$.localizacao.estado").value("Minas Gerais"))
                .andExpect(jsonPath("$.fonte").value("Open-Meteo"))
                .andExpect(jsonPath("$.atual.temperatura").value(17.5))
                .andExpect(jsonPath("$.atual.umidade").value(81))
                .andExpect(jsonPath("$.atual.ventoDirecao").value("L"))
                .andExpect(jsonPath("$.atual.descricao").value("Predominantemente limpo"))
                .andExpect(jsonPath("$.atual.unidades.temperatura").value("Celsius"));
    }

    @Test
    @DisplayName("GET /clima/belo-horizonte retorna a mesma resposta da rota principal")
    void deveRetornarClimaPelaRotaNomeada() throws Exception {
        when(climaService.consultarCidadePadrao(anyInt())).thenReturn(respostaDeExemplo());

        mockMvc.perform(get("/clima/belo-horizonte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.localizacao.cidade").value("Belo Horizonte"));
    }

    @Test
    @DisplayName("GET /clima/cidade/{cidade} consulta a cidade informada")
    void deveConsultarOutraCidade() throws Exception {
        when(climaService.consultarPorCidade(eq("Ouro Preto"), anyInt())).thenReturn(respostaDeExemplo());

        mockMvc.perform(get("/clima/cidade/{cidade}", "Ouro Preto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atual.temperatura").value(17.5));
    }

    @Test
    @DisplayName("Cidade inexistente retorna 404 com corpo de erro padronizado")
    void deveRetornar404ParaCidadeInexistente() throws Exception {
        when(climaService.consultarPorCidade(anyString(), anyInt()))
                .thenThrow(new CidadeNaoEncontradaException("xpto"));

        mockMvc.perform(get("/clima/cidade/{cidade}", "xpto"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.caminho").value("/clima/cidade/xpto"))
                .andExpect(jsonPath("$.mensagem").exists());
    }

    @Test
    @DisplayName("Falha na API externa retorna 503 em vez de estourar o erro")
    void deveRetornar503QuandoApiExternaFalha() throws Exception {
        when(climaService.consultarCidadePadrao(anyInt()))
                .thenThrow(new ClimaIndisponivelException("API de clima indisponivel."));

        mockMvc.perform(get("/clima"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503))
                .andExpect(jsonPath("$.mensagem").value("API de clima indisponivel."));
    }

    @Test
    @DisplayName("Parametro dias fora do intervalo permitido retorna 400")
    void deveRetornar400ParaQuantidadeDeDiasInvalida() throws Exception {
        mockMvc.perform(get("/clima").param("dias", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalhes").isArray());
    }

    @Test
    @DisplayName("Parametro dias com tipo invalido retorna 400")
    void deveRetornar400ParaTipoInvalido() throws Exception {
        mockMvc.perform(get("/clima").param("dias", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Rota inexistente retorna 404 em JSON, e nao 500")
    void deveRetornar404ParaRotaInexistente() throws Exception {
        mockMvc.perform(get("/rota-que-nao-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private ClimaResponse respostaDeExemplo() {
        LocalizacaoDto localizacao = new LocalizacaoDto(
                "Belo Horizonte", "Minas Gerais", "Brasil",
                -19.92083, -43.93778, 888.0, "America/Sao_Paulo");

        CondicaoAtualDto atual = new CondicaoAtualDto(
                17.5, 16.7, 26.7, 16.3, 81, 925.9, 0.0,
                14.8, 106, "L", "Leste",
                1, "PREDOMINANTEMENTE_LIMPO", "Predominantemente limpo",
                true, LocalDateTime.of(2026, 8, 24, 7, 30), UnidadesDto.PADRAO);

        return new ClimaResponse(localizacao, OffsetDateTime.now(), "Open-Meteo", atual, List.of());
    }
}
