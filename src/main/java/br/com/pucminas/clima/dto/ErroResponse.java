package br.com.pucminas.clima.dto;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Resposta padronizada de erro")
public record ErroResponse(

        @Schema(description = "Momento em que o erro ocorreu")
        OffsetDateTime timestamp,

        @Schema(description = "Código HTTP", example = "503")
        int status,

        @Schema(description = "Nome do código HTTP", example = "Service Unavailable")
        String erro,

        @Schema(description = "Mensagem explicativa para quem consome a API")
        String mensagem,

        @Schema(description = "Caminho requisitado", example = "/clima")
        String caminho,

        @Schema(description = "Detalhes de validação, quando houver")
        List<String> detalhes) {
    public static ErroResponse de(int status, String erro, String mensagem, String caminho) {
        return new ErroResponse(OffsetDateTime.now(), status, erro, mensagem, caminho, null);
    }

    public static ErroResponse de(int status, String erro, String mensagem, String caminho, List<String> detalhes) {
        return new ErroResponse(OffsetDateTime.now(), status, erro, mensagem, caminho, detalhes);
    }
}
