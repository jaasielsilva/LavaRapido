package br.com.lavajato.model.servico;

public enum StatusServico {
    EM_FILA("Em Fila", "warning"),
    EM_ANDAMENTO("Em Andamento", "primary"),
    CONCLUIDO("Concluído", "success"),
    CANCELADO("Cancelado", "danger");

    private final String descricao;
    private final String cor;

    StatusServico(String descricao, String cor) {
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
