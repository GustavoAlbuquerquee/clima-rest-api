package br.com.pucminas.clima.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Localização da cidade consultada")
public record LocalizacaoDto(

        @Schema(description = "Nome da cidade", example = "Belo Horizonte")
        String cidade,

        @Schema(description = "Estado / unidade federativa", example = "Minas Gerais")
        String estado,

        @Schema(description = "País", example = "Brasil")
        String pais,

        @Schema(description = "Latitude em graus decimais", example = "-19.92083")
        Double latitude,

        @Schema(description = "Longitude em graus decimais", example = "-43.93778")
        Double longitude,

        @Schema(description = "Altitude em metros", example = "888.0")
        Double altitudeMetros,

        @Schema(description = "Fuso horário IANA", example = "America/Sao_Paulo")
        String fusoHorario) {
}
