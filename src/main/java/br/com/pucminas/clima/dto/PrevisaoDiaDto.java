package br.com.pucminas.clima.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Previsão do tempo para um dia")
public record PrevisaoDiaDto(

        @Schema(description = "Data da previsão", example = "2026-08-24")
        LocalDate data,

        @Schema(description = "Temperatura máxima prevista em Celsius", example = "26.7")
        Double temperaturaMaxima,

        @Schema(description = "Temperatura mínima prevista em Celsius", example = "16.3")
        Double temperaturaMinima,

        @Schema(description = "Precipitação total prevista em mm", example = "3.2")
        Double precipitacaoTotal,

        @Schema(description = "Maior probabilidade de precipitação no dia, em porcentagem", example = "65")
        Integer probabilidadePrecipitacao,

        @Schema(description = "Velocidade máxima do vento em km/h", example = "22.4")
        Double ventoVelocidadeMaxima,

        @Schema(description = "Código WMO da condição predominante", example = "80")
        Integer codigoCondicao,

        @Schema(description = "Condição climática normalizada", example = "PANCADAS_DE_CHUVA_FRACAS")
        String condicao,

        @Schema(description = "Descrição da condição do tempo", example = "Pancadas de chuva fracas")
        String descricao,


        @Schema(description = "Horário do nascer do sol", example = "06:32:00")
        LocalTime nascerDoSol,

        @Schema(description = "Horário do pôr do sol", example = "17:48:00")
        LocalTime porDoSol) {
}
