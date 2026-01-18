package br.com.lavajato.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemVendaDTO {
    private Long produtoId;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}
