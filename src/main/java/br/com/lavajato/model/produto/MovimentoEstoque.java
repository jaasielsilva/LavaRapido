package br.com.lavajato.model.produto;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movimentos_estoque")
public class MovimentoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMovimentoEstoque tipo;

    @Column(nullable = false)
    private Integer quantidade;

    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;

    @Column(length = 50)
    private String documento; // Número da nota, pedido, etc.

    @Column(nullable = false)
    private LocalDateTime data = LocalDateTime.now();

    @Column(length = 50)
    private String origem; // SCAN, AJUSTE, VENDA

    @ManyToOne
    @JoinColumn(name = "criado_por_id")
    private Usuario criadoPor;
}
