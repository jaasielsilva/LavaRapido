package br.com.lavajato.model.servico;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "servicos_avulsos")
public class ServicoAvulso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusServico status = StatusServico.EM_FILA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Servico servico;

    // Campos históricos para garantir que o preço não mude se o catálogo mudar
    private String nomeServicoHistorico;
    private BigDecimal valor;

    // Cliente Cadastrado (Opcional)
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Cliente cliente;

    // Cliente Avulso (Opcional)
    private String clienteAvulsoNome;
    private String clienteAvulsoVeiculo; // Modelo - Placa
    
    public String getNomeClienteDisplay() {
        if (cliente != null) {
            return cliente.getNome();
        }
        return clienteAvulsoNome != null ? clienteAvulsoNome : "Cliente Avulso";
    }
    
    public String getVeiculoDisplay() {
        if (cliente != null && !cliente.getVeiculos().isEmpty()) {
            // Pega o primeiro veículo para simplificar na listagem se não foi selecionado especificamente
            // Idealmente teríamos o Veículo selecionado aqui também, mas vamos simplificar
            var v = cliente.getVeiculos().get(0);
            return v.getModelo() + " - " + v.getPlaca();
        }
        return clienteAvulsoVeiculo != null ? clienteAvulsoVeiculo : "N/A";
    }
}
