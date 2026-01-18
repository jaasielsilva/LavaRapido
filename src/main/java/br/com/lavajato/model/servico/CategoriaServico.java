package br.com.lavajato.model.servico;

public enum CategoriaServico {
    LAVAGEM("Lavagem", "info"),
    POLIMENTO("Polimento", "warning"),
    ESTETICA("Estética", "success"),
    OUTROS("Outros", "secondary");

    private final String descricao;
    private final String cor; // Para classes CSS (bg-info, text-info, etc)

    CategoriaServico(String descricao, String cor) {
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
