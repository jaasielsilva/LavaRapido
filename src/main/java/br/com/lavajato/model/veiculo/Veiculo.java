package br.com.lavajato.model.veiculo;

import br.com.lavajato.model.cliente.Cliente;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A placa é obrigatória")
    @Size(min = 7, max = 8, message = "A placa deve ter 7 caracteres (ex: ABC1234)")
    @Column(nullable = false)
    private String placa;

    @NotBlank(message = "O modelo é obrigatório")
    private String modelo;
    
    private String cor;
    
    private Integer ano;

    @NotNull(message = "O cliente é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Cliente cliente;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;
}
