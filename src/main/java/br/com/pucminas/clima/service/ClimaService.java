package br.com.pucminas.clima.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.pucminas.clima.client.OpenMeteoClient;
import br.com.pucminas.clima.client.dto.OpenMeteoForecastResponse;
import br.com.pucminas.clima.client.dto.OpenMeteoGeocodingResponse;
import br.com.pucminas.clima.config.OpenMeteoProperties;
import br.com.pucminas.clima.dto.ClimaResponse;
import br.com.pucminas.clima.dto.CondicaoAtualDto;
import br.com.pucminas.clima.dto.CondicaoAtualDto.UnidadesDto;
import br.com.pucminas.clima.dto.LocalizacaoDto;
import br.com.pucminas.clima.dto.PrevisaoDiaDto;
import br.com.pucminas.clima.exception.CidadeNaoEncontradaException;
import br.com.pucminas.clima.util.CondicaoClimatica;
import br.com.pucminas.clima.util.DirecaoVento;

@Service
public class ClimaService {
    private static final Logger log = LoggerFactory.getLogger(ClimaService.class);

    private static final String FONTE = "Open-Meteo";

    private final OpenMeteoClient client;
    private final OpenMeteoProperties propriedades;

    public ClimaService(OpenMeteoClient client, OpenMeteoProperties propriedades) {
        this.client = client;
        this.propriedades = propriedades;
    }

    public ClimaResponse consultarCidadePadrao(int dias) {
        OpenMeteoProperties.CidadePadrao bh = propriedades.cidadePadrao();
        log.info("Consultando clima da cidade padrao: {} ({} dias de previsao)", bh.nome(), dias);

        OpenMeteoForecastResponse resposta =
                client.buscarPrevisao(bh.latitude(), bh.longitude(), bh.fusoHorario(), dias);

        LocalizacaoDto localizacao = new LocalizacaoDto(
                bh.nome(),
                bh.estado(),
                bh.pais(),
                resposta.latitude(),
                resposta.longitude(),
                resposta.elevation(),
                resposta.timezone() != null ? resposta.timezone() : bh.fusoHorario());

        return montarResposta(localizacao, resposta);
    }

    public ClimaResponse consultarPorCidade(String cidade, int dias) {
        log.info("Consultando clima da cidade informada: {} ({} dias de previsao)", cidade, dias);

        OpenMeteoGeocodingResponse geocoding = client.geocodificar(cidade);
        if (geocoding.vazia()) {
            throw new CidadeNaoEncontradaException(cidade);
        }

        OpenMeteoGeocodingResponse.Resultado local = geocoding.results().get(0);
        String fusoHorario = local.timezone() != null ? local.timezone() : "auto";

        OpenMeteoForecastResponse resposta =
                client.buscarPrevisao(local.latitude(), local.longitude(), fusoHorario, dias);

        LocalizacaoDto localizacao = new LocalizacaoDto(
                local.name(),
                local.admin1(),
                local.country(),
                resposta.latitude() != null ? resposta.latitude() : local.latitude(),
                resposta.longitude() != null ? resposta.longitude() : local.longitude(),
                resposta.elevation() != null ? resposta.elevation() : local.elevation(),
                resposta.timezone() != null ? resposta.timezone() : fusoHorario);

        return montarResposta(localizacao, resposta);
    }

    private ClimaResponse montarResposta(LocalizacaoDto localizacao, OpenMeteoForecastResponse resposta) {
        List<PrevisaoDiaDto> previsao = converterPrevisao(resposta.daily());

        PrevisaoDiaDto hoje = previsao.isEmpty() ? null : previsao.get(0);

        return new ClimaResponse(
                localizacao,
                agoraNoFusoDaCidade(localizacao.fusoHorario()),
                FONTE,
                converterCondicaoAtual(resposta.current(), hoje),
                previsao);
    }

    private CondicaoAtualDto converterCondicaoAtual(OpenMeteoForecastResponse.Current atual, PrevisaoDiaDto hoje) {
        CondicaoClimatica condicao = CondicaoClimatica.porCodigo(atual.codigoCondicao());
        DirecaoVento direcao = DirecaoVento.porGraus(atual.ventoDirecaoGraus());

        return new CondicaoAtualDto(
                atual.temperatura(),
                atual.sensacaoTermica(),
                hoje != null ? hoje.temperaturaMaxima() : null,
                hoje != null ? hoje.temperaturaMinima() : null,
                atual.umidade(),
                atual.pressao(),
                atual.precipitacao(),
                atual.ventoVelocidade(),
                atual.ventoDirecaoGraus(),
                direcao != null ? direcao.getSigla() : null,
                direcao != null ? direcao.getExtenso() : null,
                atual.codigoCondicao(),
                condicao.name(),
                condicao.getDescricao(),
                atual.diurno() == null ? null : atual.diurno() == 1,
                converterDataHora(atual.time()),
                UnidadesDto.PADRAO);
    }

    private List<PrevisaoDiaDto> converterPrevisao(OpenMeteoForecastResponse.Daily diario) {
        if (diario == null || diario.time() == null || diario.time().isEmpty()) {
            return List.of();
        }

        List<PrevisaoDiaDto> dias = new ArrayList<>(diario.time().size());
        for (int i = 0; i < diario.time().size(); i++) {
            CondicaoClimatica condicao = CondicaoClimatica.porCodigo(valorEm(diario.codigoCondicao(), i));

            dias.add(new PrevisaoDiaDto(
                    converterData(diario.time().get(i)),
                    valorEm(diario.temperaturaMaxima(), i),
                    valorEm(diario.temperaturaMinima(), i),
                    valorEm(diario.precipitacaoTotal(), i),
                    valorEm(diario.probabilidadePrecipitacao(), i),
                    valorEm(diario.ventoVelocidadeMaxima(), i),
                    valorEm(diario.codigoCondicao(), i),
                    condicao.name(),
                    condicao.getDescricao(),
                    converterHora(valorEm(diario.nascerDoSol(), i)),
                    converterHora(valorEm(diario.porDoSol(), i))));
        }
        return List.copyOf(dias);
    }

    private <T> T valorEm(List<T> lista, int indice) {
        if (lista == null || indice >= lista.size()) {
            return null;
        }
        return lista.get(indice);
    }

    private LocalDate converterData(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            log.warn("Data em formato inesperado recebida da API externa: {}", texto);
            return null;
        }
    }

    private LocalDateTime converterDataHora(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(texto);
        } catch (DateTimeParseException e) {
            log.warn("Data/hora em formato inesperado recebida da API externa: {}", texto);
            return null;
        }
    }

    private LocalTime converterHora(String texto) {
        LocalDateTime dataHora = converterDataHora(texto);
        return dataHora == null ? null : dataHora.toLocalTime();
    }

    private OffsetDateTime agoraNoFusoDaCidade(String fusoHorario) {
        try {
            return OffsetDateTime.now(ZoneId.of(fusoHorario));
        } catch (RuntimeException e) {
            log.warn("Fuso horario invalido ({}), usando o fuso do servidor.", fusoHorario);
            return OffsetDateTime.now();
        }
    }
}
