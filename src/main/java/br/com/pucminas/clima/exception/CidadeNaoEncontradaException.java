package br.com.pucminas.clima.exception;

public class CidadeNaoEncontradaException extends RuntimeException {
    private final String cidadeInformada;

    public CidadeNaoEncontradaException(String cidadeInformada) {
        super("Nenhuma cidade encontrada com o nome '%s'.".formatted(cidadeInformada));
        this.cidadeInformada = cidadeInformada;
    }

    public String getCidadeInformada() {
        return cidadeInformada;
    }
}
