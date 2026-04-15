package br.com.nfq.backend.entity;

import java.math.BigDecimal;
import java.util.List;

public record TransacaoReport (
    String nomeLoja,
    BigDecimal total,
    List<Transacao> transacoes
){
    public TransacaoReport addTotal(BigDecimal valor){
        return new TransacaoReport(nomeLoja, total.add(valor), transacoes);
    }

    public TransacaoReport addTransacao(Transacao transacao){
        transacoes.add(transacao);
        return new TransacaoReport(nomeLoja, total, transacoes);
    }
}
