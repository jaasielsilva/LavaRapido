package br.com.lavajato.model.agendamento;

import br.com.lavajato.model.veiculo.Veiculo;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @Column(columnDefinition = "TEXT")
    private String servicos;

    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean usoFidelidade = false;
}
