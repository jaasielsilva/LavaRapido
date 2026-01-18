package br.com.lavajato.model.servico;

import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "servicos")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaServico categoria;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(name = "tempo_minutos", nullable = false)
    private Integer tempoMinutos;

    private boolean ativo = true;
}
