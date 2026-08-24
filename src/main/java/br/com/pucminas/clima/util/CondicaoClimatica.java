package br.com.pucminas.clima.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum CondicaoClimatica {
    CEU_LIMPO(0, "Céu limpo"),
    PREDOMINANTEMENTE_LIMPO(1, "Predominantemente limpo"),
    PARCIALMENTE_NUBLADO(2, "Parcialmente nublado"),
    NUBLADO(3, "Nublado"),
    NEVOEIRO(45, "Nevoeiro"),
    NEVOEIRO_COM_GELO(48, "Nevoeiro com deposição de gelo"),
    GAROA_FRACA(51, "Garoa fraca"),
    GAROA_MODERADA(53, "Garoa moderada"),
    GAROA_FORTE(55, "Garoa forte"),
    GAROA_CONGELANTE_FRACA(56, "Garoa congelante fraca"),
    GAROA_CONGELANTE_FORTE(57, "Garoa congelante forte"),
    CHUVA_FRACA(61, "Chuva fraca"),
    CHUVA_MODERADA(63, "Chuva moderada"),
    CHUVA_FORTE(65, "Chuva forte"),
    CHUVA_CONGELANTE_FRACA(66, "Chuva congelante fraca"),
    CHUVA_CONGELANTE_FORTE(67, "Chuva congelante forte"),
    NEVE_FRACA(71, "Neve fraca"),
    NEVE_MODERADA(73, "Neve moderada"),
    NEVE_FORTE(75, "Neve forte"),
    GRAOS_DE_NEVE(77, "Grãos de neve"),
    PANCADAS_DE_CHUVA_FRACAS(80, "Pancadas de chuva fracas"),
    PANCADAS_DE_CHUVA_MODERADAS(81, "Pancadas de chuva moderadas"),
    PANCADAS_DE_CHUVA_VIOLENTAS(82, "Pancadas de chuva violentas"),
    PANCADAS_DE_NEVE_FRACAS(85, "Pancadas de neve fracas"),
    PANCADAS_DE_NEVE_FORTES(86, "Pancadas de neve fortes"),
    TROVOADA(95, "Trovoada"),
    TROVOADA_COM_GRANIZO_FRACO(96, "Trovoada com granizo fraco"),
    TROVOADA_COM_GRANIZO_FORTE(99, "Trovoada com granizo forte"),
    DESCONHECIDA(-1, "Condição não informada");

    private static final Map<Integer, CondicaoClimatica> POR_CODIGO = Stream.of(values())
            .collect(Collectors.toUnmodifiableMap(CondicaoClimatica::getCodigo, condicao -> condicao));

    private final int codigo;
    private final String descricao;

    CondicaoClimatica(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static CondicaoClimatica porCodigo(Integer codigo) {
        if (codigo == null) {
            return DESCONHECIDA;
        }
        return POR_CODIGO.getOrDefault(codigo, DESCONHECIDA);
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

}
