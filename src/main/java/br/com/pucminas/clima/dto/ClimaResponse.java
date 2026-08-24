package br.com.pucminas.clima.dto;

import java.time.OffsetDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informações meteorológicas processadas pela aplicação")
public record ClimaResponse(

        @Schema(description = "Localização da cidade consultada")
        LocalizacaoDto localizacao,

        @Schema(description = "Data e horário em que esta consulta foi realizada")
        OffsetDateTime consultadoEm,

        @Schema(description = "Serviço externo utilizado como fonte dos dados", example = "Open-Meteo")
        String fonte,

        @Schema(description = "Condições meteorológicas atuais")
        CondicaoAtualDto atual,

        @Schema(description = "Previsão para os próximos dias")
        List<PrevisaoDiaDto> previsao) {
}
