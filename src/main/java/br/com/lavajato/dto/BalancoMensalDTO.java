package br.com.lavajato.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;

@Data
@Builder
public class BalancoMensalDTO {
    private YearMonth mes; // Para ordenação e formatação (ex: "Fevereiro 2025")
    
    private BigDecimal receitaServicos;
    private BigDecimal receitaAgendamentos;
    private BigDecimal receitaProdutos;
    private BigDecimal receitaTotal;
    
    private BigDecimal custosProdutos; // Custo de Mercadoria Vendida (CMV)
    private BigDecimal despesasOperacionais; // Saídas manuais
    private BigDecimal custosTotal; // Soma de custos + despesas
    
    private BigDecimal lucroLiquido;
    private BigDecimal margemLucro; // Percentual
}
