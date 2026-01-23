package br.com.lavajato.model.usuario;

public enum TipoPagamento {
    MENSAL("Mensal"),
    QUINZENAL("Quinzenal"),
    SEMANAL("Semanal"),
    DIARIA("Diária"),
    HORA("Por Hora");

    private final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
