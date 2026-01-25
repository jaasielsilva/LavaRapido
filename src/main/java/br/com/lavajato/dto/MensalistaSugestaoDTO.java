package br.com.lavajato.dto;

import java.math.BigDecimal;

public record MensalistaSugestaoDTO(
        Long id,
        String nomeCliente,
        String telefone,
        Integer diaVencimento,
        BigDecimal valorMensal,
        String status
) {}
