package br.com.lavajato.model.servico;

import java.math.BigDecimal;

public enum TipoServico {
    LAVAGEM_SIMPLES("Lavagem Simples", 30, new BigDecimal("40.00")),
    LAVAGEM_COMPLETA("Lavagem Completa", 60, new BigDecimal("70.00")),
    LAVAGEM_PREMIUM("Lavagem Premium", 90, new BigDecimal("120.00")),
    POLIMENTO_TECNICO("Polimento Técnico", 240, new BigDecimal("350.00")),
    CRISTALIZACAO("Cristalização", 180, new BigDecimal("280.00")),
    VITRIFICACAO("Vitrificação", 480, new BigDecimal("800.00")),
    HIGIENIZACAO_INTERNA("Higienização Interna", 120, new BigDecimal("180.00")),
    HIDRATACAO_COURO("Hidratação de Couro", 90, new BigDecimal("150.00")); // Preço estimado, já que o prompt cortou

    private final String descricao;
    private final int tempoMinutos;
    private final BigDecimal preco;

    TipoServico(String descricao, int tempoMinutos, BigDecimal preco) {
        this.descricao = descricao;
        this.tempoMinutos = tempoMinutos;
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getTempoMinutos() {
        return tempoMinutos;
    }

    public BigDecimal getPreco() {
        return preco;
    }
}
