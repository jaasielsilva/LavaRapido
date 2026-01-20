package br.com.lavajato.dto;

import br.com.lavajato.model.financeiro.TipoLancamento;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class MovimentacaoDTO {
    private String id; // V-1, L-1
    private LocalDateTime data;
    private String descricao;
    private BigDecimal valor;
    private TipoLancamento tipo;
    private String categoria;
    private String origem; // VENDA, MANUAL
}
