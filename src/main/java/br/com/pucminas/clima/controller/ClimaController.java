package br.com.pucminas.clima.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.pucminas.clima.dto.ClimaResponse;
import br.com.pucminas.clima.dto.ErroResponse;
import br.com.pucminas.clima.service.ClimaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping(value = "/clima", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Clima", description = "Consulta de informações meteorológicas")
public class ClimaController {
    private static final int DIAS_PADRAO = 7;

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    @GetMapping
    @Operation(summary = "Clima de Belo Horizonte - MG",
            description = "Retorna as condições meteorológicas atuais e a previsão para os próximos dias "
                    + "da cidade de Belo Horizonte - MG.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados meteorológicos retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "503", description = "API externa de clima indisponível",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ClimaResponse climaBeloHorizonte(
            @Parameter(description = "Quantidade de dias de previsão (1 a 16)", example = "7")
            @RequestParam(name = "dias", defaultValue = "" + DIAS_PADRAO)
            @Min(value = 1, message = "O parametro dias deve ser no minimo 1.")
            @Max(value = 16, message = "O parametro dias deve ser no maximo 16.")
            int dias) {
        return climaService.consultarCidadePadrao(dias);
    }

    @GetMapping("/belo-horizonte")
    @Operation(summary = "Clima de Belo Horizonte - MG (rota nomeada)",
            description = "Mesma resposta de GET /clima, com a cidade explícita no caminho.")
    public ClimaResponse climaBeloHorizonteRotaNomeada(
            @Parameter(description = "Quantidade de dias de previsão (1 a 16)", example = "7")
            @RequestParam(name = "dias", defaultValue = "" + DIAS_PADRAO)
            @Min(value = 1, message = "O parametro dias deve ser no minimo 1.")
            @Max(value = 16, message = "O parametro dias deve ser no maximo 16.")
            int dias) {
        return climaService.consultarCidadePadrao(dias);
    }

    @GetMapping("/cidade/{cidade}")
    @Operation(summary = "Clima de uma cidade qualquer",
            description = "Traduz o nome da cidade em coordenadas pela API de geocodificação da Open-Meteo "
                    + "e retorna as condições meteorológicas do local encontrado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados meteorológicos retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Cidade não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class))),
            @ApiResponse(responseCode = "503", description = "API externa de clima indisponível",
                    content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    })
    public ClimaResponse climaPorCidade(
            @Parameter(description = "Nome da cidade", example = "Ouro Preto")
            @PathVariable
            @NotBlank(message = "O nome da cidade nao pode ser vazio.")
            @Size(min = 2, max = 80, message = "O nome da cidade deve ter entre 2 e 80 caracteres.")
            String cidade,

            @Parameter(description = "Quantidade de dias de previsão (1 a 16)", example = "7")
            @RequestParam(name = "dias", defaultValue = "" + DIAS_PADRAO)
            @Min(value = 1, message = "O parametro dias deve ser no minimo 1.")
            @Max(value = 16, message = "O parametro dias deve ser no maximo 16.")
            int dias) {
        return climaService.consultarPorCidade(cidade, dias);
    }
}
