package br.com.lavajato.model.mensalista;

import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pagamentos_mensalistas")
public class PagamentoMensalista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mensalista_id")
    private Mensalista mensalista;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @Column(name = "valor_pago", nullable = false)
    private BigDecimal valorPago;

    @Column(name = "competencia", nullable = false)
    private String competencia; // Ex: "01/2024"

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro = LocalDateTime.now();
}
