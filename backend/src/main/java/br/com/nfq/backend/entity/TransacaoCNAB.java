package br.com.nfq.backend.entity;

import java.math.BigDecimal;

/**
 * DTO do modelo CNAB fornecido pelo desafio
 * @param tipo
 * @param data
 * @param valor
 * @param cpf
 * @param cartao
 * @param hora
 * @param donoLoja
 * @param nomeLoja
 */
public record TransacaoCNAB(
    Integer tipo,
    String data,
    BigDecimal valor,
    Long cpf, //nao gosto porque desconsidera zero À esquerda
    String cartao,
    String hora,
    String donoLoja,
    String nomeLoja
) {
}
