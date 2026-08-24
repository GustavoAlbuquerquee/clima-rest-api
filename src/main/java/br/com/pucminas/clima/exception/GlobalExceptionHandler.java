package br.com.pucminas.clima.exception;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.pucminas.clima.dto.ErroResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ClimaIndisponivelException.class)
    public ResponseEntity<ErroResponse> tratarClimaIndisponivel(ClimaIndisponivelException e,
                                                                HttpServletRequest requisicao) {
        log.error("Servico de clima indisponivel: {}", e.getMessage());
        return construir(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), requisicao);
    }

    @ExceptionHandler(CidadeNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> tratarCidadeNaoEncontrada(CidadeNaoEncontradaException e,
                                                                  HttpServletRequest requisicao) {
        log.warn("Cidade nao encontrada: {}", e.getCidadeInformada());
        return construir(HttpStatus.NOT_FOUND, e.getMessage(), requisicao);
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ErroResponse> tratarRequisicaoInvalida(RequisicaoInvalidaException e,
                                                                 HttpServletRequest requisicao) {
        log.warn("Requisicao recusada pela API externa: {}", e.getMessage());
        return construir(HttpStatus.BAD_GATEWAY, e.getMessage(), requisicao);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(ConstraintViolationException e,
                                                        HttpServletRequest requisicao) {
        List<String> detalhes = e.getConstraintViolations().stream()
                .map(violacao -> violacao.getMessage())
                .toList();

        ErroResponse corpo = ErroResponse.de(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Um ou mais parametros informados sao invalidos.",
                requisicao.getRequestURI(),
                detalhes);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoInvalido(MethodArgumentTypeMismatchException e,
                                                           HttpServletRequest requisicao) {
        String mensagem = "O parametro %s recebeu um valor invalido: %s".formatted(e.getName(), e.getValue());
        return construir(HttpStatus.BAD_REQUEST, mensagem, requisicao);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroAusente(MissingServletRequestParameterException e,
                                                               HttpServletRequest requisicao) {
        String mensagem = "O parametro obrigatorio %s nao foi informado.".formatted(e.getParameterName());
        return construir(HttpStatus.BAD_REQUEST, mensagem, requisicao);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> tratarRotaInexistente(NoResourceFoundException e,
                                                              HttpServletRequest requisicao) {
        String mensagem = "Nenhum endpoint encontrado para %s %s."
                .formatted(requisicao.getMethod(), requisicao.getRequestURI());
        return construir(HttpStatus.NOT_FOUND, mensagem, requisicao);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMetodoNaoSuportado(HttpRequestMethodNotSupportedException e,
                                                                 HttpServletRequest requisicao) {
        String mensagem = "O metodo %s nao e suportado neste endpoint.".formatted(e.getMethod());
        return construir(HttpStatus.METHOD_NOT_ALLOWED, mensagem, requisicao);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception e, HttpServletRequest requisicao) {
        log.error("Erro inesperado ao processar {}", requisicao.getRequestURI(), e);
        return construir(HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro inesperado ao processar a requisicao.", requisicao);
    }

    private ResponseEntity<ErroResponse> construir(HttpStatus status, String mensagem,
                                                   HttpServletRequest requisicao) {
        ErroResponse corpo = ErroResponse.de(
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                requisicao.getRequestURI());

        return ResponseEntity.status(status).body(corpo);
    }
}
