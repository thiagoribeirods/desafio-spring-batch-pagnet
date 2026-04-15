package br.com.nfq.backend.enums;

import lombok.Getter;

@Getter
public enum TipoTransacaoEnum {
    DEBITO(1, +1),
    BOLETO(2,-1),
    FINANCIAMENTO(3, -1),
    CREDITO(4, +1),
    RECEBIMENTO_EMPRESTIMO(5, +1),
    VENDAS(6, +1),
    TED(7, +1),
    DOC(8, +1),
    ALUGUEL(9, -1);

    private final int tipo;
    private final int sinal;

    TipoTransacaoEnum(int tipo, int sinal) {
        this.tipo = tipo;
        this.sinal = sinal;
    }

    public static TipoTransacaoEnum fromTipo(int tipo) {
        for(TipoTransacaoEnum t : values()) {
            if(t.tipo == tipo) {
                return t;
            }
        }

        throw new IllegalArgumentException();
    }
}
