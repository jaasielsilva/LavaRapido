package br.com.lavajato.model.mensalista;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mensalistas")
public class Mensalista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(name = "dia_vencimento", nullable = false)
    private Integer diaVencimento;

    @Column(name = "valor_mensal", nullable = false)
    private BigDecimal valorMensal;

    @Column(name = "data_ultimo_pagamento")
    private LocalDate dataUltimoPagamento;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean ativo = true;

    // Soft delete logic
    public void excluir() {
        this.ativo = false;
    }
}
