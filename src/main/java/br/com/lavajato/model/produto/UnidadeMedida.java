package br.com.lavajato.model.produto;

public enum UnidadeMedida {
    UNIDADE("Unidade", "un"),
    LITRO("Litro", "L"),
    MILILITRO("Mililitro", "ml"),
    GRAMA("Grama", "g"),
    QUILOGRAMA("Quilograma", "kg"),
    KIT("Kit", "kt"),
    CAIXA("Caixa", "cx");

    private final String descricao;
    private final String sigla;

    UnidadeMedida(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSigla() {
        return sigla;
    }
}
