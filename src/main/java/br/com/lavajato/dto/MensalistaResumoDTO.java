package br.com.lavajato.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensalistaResumoDTO {
    private long totalAtivos;
    private BigDecimal receitaPrevista;
    private BigDecimal recebidoMes;
}
