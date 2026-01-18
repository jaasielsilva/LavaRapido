package br.com.lavajato.dto;

import br.com.lavajato.model.venda.FormaPagamento;
import lombok.Data;
import java.util.List;

@Data
public class VendaDTO {
    private Long clienteId;
    private FormaPagamento formaPagamento;
    private List<ItemVendaDTO> itens;
}
