package br.com.lavajato.model.cliente;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.veiculo.Veiculo;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String telefone;
    
    private String email;
    
    @Column(name = "data_cadastro")
    private LocalDate dataCadastro = LocalDate.now();

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "cliente")
    private List<Veiculo> veiculos = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer quantidadeLavagens = 0;

    @Column(name = "data_inicio_fidelidade")
    private LocalDate dataInicioFidelidade;
}
