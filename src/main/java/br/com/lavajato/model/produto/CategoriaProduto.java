package br.com.lavajato.model.produto;

public enum CategoriaProduto {
    CERA("Cera", "warning"),
    SHAMPOO("Shampoo", "primary"),
    SILICONE("Silicone", "info"),
    AROMATIZANTE("Aromatizante", "danger"), // Rosa/Vermelho
    ACESSORIO("Acessório", "success"),
    OUTROS("Outros", "secondary");

    private final String descricao;
    private final String cor;

    CategoriaProduto(String descricao, String cor) {
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
