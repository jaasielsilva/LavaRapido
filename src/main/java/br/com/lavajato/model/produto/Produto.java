package br.com.lavajato.model.produto;

import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal precoVenda;

    private BigDecimal precoCusto;

    @Column(nullable = false)
    private Integer estoque;

    private Integer estoqueMinimo = 5;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProduto categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidade;

    private boolean ativo = true;
}
