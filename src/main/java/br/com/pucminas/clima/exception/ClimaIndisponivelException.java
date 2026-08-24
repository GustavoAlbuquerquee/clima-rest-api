package br.com.pucminas.clima.exception;

public class ClimaIndisponivelException extends RuntimeException {
    public ClimaIndisponivelException(String mensagem) {
        super(mensagem);
    }

    public ClimaIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
