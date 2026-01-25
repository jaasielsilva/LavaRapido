package br.com.lavajato.service.venda;

import br.com.lavajato.dto.ItemVendaDTO;
import br.com.lavajato.dto.VendaDTO;
import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.produto.Produto;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.model.venda.ItemVenda;
import br.com.lavajato.model.venda.Venda;
import br.com.lavajato.repository.venda.VendaRepository;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.produto.ProdutoService;
import br.com.lavajato.service.usuario.UsuarioService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Transactional
    public void registrarVenda(VendaDTO vendaDTO) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        Venda venda = new Venda();
        venda.setEmpresa(usuario.getEmpresa());
        venda.setDataVenda(LocalDateTime.now());
        venda.setFormaPagamento(vendaDTO.getFormaPagamento());

        if (vendaDTO.getClienteId() != null) {
            Cliente cliente = clienteService.buscarPorId(vendaDTO.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            venda.setCliente(cliente);
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ItemVendaDTO itemDTO : vendaDTO.getItens()) {
            Produto produto = produtoService.buscarPorId(itemDTO.getProdutoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Baixa no estoque
            if (produto.getEstoque() < itemDTO.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            produto.setEstoque(produto.getEstoque() - itemDTO.getQuantidade());
            produtoService.salvar(produto);

            ItemVenda item = new ItemVenda();
            item.setProduto(produto);
            item.setQuantidade(itemDTO.getQuantidade());
            item.setPrecoUnitario(itemDTO.getPrecoUnitario());
            item.setSubtotal(itemDTO.getPrecoUnitario().multiply(BigDecimal.valueOf(itemDTO.getQuantidade())));

            venda.adicionarItem(item);
            total = total.add(item.getSubtotal());
        }

        venda.setValorTotal(total);
        vendaRepository.save(venda);
    }

    public BigDecimal calcularFaturamentoHoje(Empresa empresa) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal valor = vendaRepository.sumFaturamentoPorPeriodo(empresa, inicio, fim);
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public BigDecimal calcularFaturamentoOntem(Empresa empresa) {
        LocalDate ontem = LocalDate.now().minusDays(1);
        LocalDateTime inicio = ontem.atStartOfDay();
        LocalDateTime fim = ontem.atTime(LocalTime.MAX);
        BigDecimal valor = vendaRepository.sumFaturamentoPorPeriodo(empresa, inicio, fim);
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
