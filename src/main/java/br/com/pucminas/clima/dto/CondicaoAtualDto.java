package br.com.pucminas.clima.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Condições meteorológicas atuais")
public record CondicaoAtualDto(

        @Schema(description = "Temperatura atual em graus Celsius", example = "17.5")
        Double temperatura,

        @Schema(description = "Sensação térmica em graus Celsius", example = "16.7")
        Double sensacaoTermica,

        @Schema(description = "Temperatura máxima prevista para hoje", example = "26.7")
        Double temperaturaMaxima,

        @Schema(description = "Temperatura mínima prevista para hoje", example = "16.3")
        Double temperaturaMinima,

        @Schema(description = "Umidade relativa do ar em porcentagem", example = "81")
        Integer umidade,

        @Schema(description = "Pressão atmosférica na superfície em hPa", example = "925.9")
        Double pressao,

        @Schema(description = "Precipitação acumulada na última hora em mm", example = "0.0")
        Double precipitacao,

        @Schema(description = "Velocidade do vento em km/h", example = "14.8")
        Double ventoVelocidade,

        @Schema(description = "Direção do vento em graus, sendo 0 o Norte", example = "106")
        Integer ventoDirecaoGraus,

        @Schema(description = "Direção do vento na rosa dos ventos", example = "L")
        String ventoDirecao,

        @Schema(description = "Direção do vento por extenso", example = "Leste")
        String ventoDirecaoExtenso,

        @Schema(description = "Código WMO da condição climática", example = "1")
        Integer codigoCondicao,

        @Schema(description = "Condição climática normalizada", example = "PREDOMINANTEMENTE_LIMPO")
        String condicao,

        @Schema(description = "Descrição da condição do tempo", example = "Predominantemente limpo")
        String descricao,


        @Schema(description = "Indica se a leitura foi feita durante o dia", example = "true")
        Boolean diurno,

        @Schema(description = "Instante da leitura no fuso horário da cidade", example = "2026-08-24T07:30:00")
        LocalDateTime observadoEm,

        @Schema(description = "Unidades de medida utilizadas nos campos acima")
        UnidadesDto unidades) {
    @Schema(description = "Unidades de medida")
    public record UnidadesDto(String temperatura, String umidade, String pressao,
                              String precipitacao, String ventoVelocidade, String ventoDirecao) {
        public static final UnidadesDto PADRAO =
                new UnidadesDto("Celsius", "%", "hPa", "mm", "km/h", "graus");
    }
}
