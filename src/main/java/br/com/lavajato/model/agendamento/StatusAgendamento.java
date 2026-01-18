package br.com.lavajato.model.agendamento;

public enum StatusAgendamento {
    AGENDADO("Agendado", "primary"),
    EM_ANDAMENTO("Em Andamento", "warning"),
    CONCLUIDO("Concluído", "success"),
    CANCELADO("Cancelado", "danger");

    private final String descricao;
    private final String cor;

    StatusAgendamento(String descricao, String cor) {
        this.descricao = descricao;
        this.cor = cor;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCor() {
        return cor;
    }
}
