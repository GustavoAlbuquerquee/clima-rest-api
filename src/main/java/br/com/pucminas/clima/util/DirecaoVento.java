package br.com.pucminas.clima.util;

public enum DirecaoVento {
    N("N", "Norte"),
    NNE("NNE", "Nor-nordeste"),
    NE("NE", "Nordeste"),
    ENE("ENE", "Lés-nordeste"),
    L("L", "Leste"),
    ESE("ESE", "Lés-sudeste"),
    SE("SE", "Sudeste"),
    SSE("SSE", "Su-sudeste"),
    S("S", "Sul"),
    SSO("SSO", "Su-sudoeste"),
    SO("SO", "Sudoeste"),
    OSO("OSO", "Oés-sudoeste"),
    O("O", "Oeste"),
    ONO("ONO", "Oés-noroeste"),
    NO("NO", "Noroeste"),
    NNO("NNO", "Nor-noroeste");

    private static final double TAMANHO_SETOR = 360.0 / values().length;

    private final String sigla;
    private final String extenso;

    DirecaoVento(String sigla, String extenso) {
        this.sigla = sigla;
        this.extenso = extenso;
    }

    public static DirecaoVento porGraus(Integer graus) {
        if (graus == null) {
            return null;
        }
        double normalizado = ((graus % 360) + 360) % 360;

        int indice = (int) Math.floor((normalizado + TAMANHO_SETOR / 2) / TAMANHO_SETOR) % values().length;
        return values()[indice];
    }

    public String getSigla() {
        return sigla;
    }

    public String getExtenso() {
        return extenso;
    }
}
