package br.com.lavajato.controller.produto;

import br.com.lavajato.model.produto.CategoriaProduto;
import br.com.lavajato.model.produto.Produto;
import br.com.lavajato.model.produto.UnidadeMedida;
import br.com.lavajato.model.venda.FormaPagamento;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import br.com.lavajato.model.financeiro.TipoLancamento;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.produto.ProdutoService;
import br.com.lavajato.service.financeiro.FinanceiroService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService service;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String listar(Model model,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria) {

        CategoriaProduto categoriaEnum = null;
        if (StringUtils.hasText(categoria)) {
            try {
                categoriaEnum = CategoriaProduto.valueOf(categoria);
            } catch (IllegalArgumentException ex) {
                categoriaEnum = null;
            }
        }

        Page<Produto> page = service.listarAtivos(pageable, busca, categoriaEnum);
        model.addAttribute("produtos", page);
        model.addAttribute("totalProdutos", service.contarProdutos());
        model.addAttribute("estoqueBaixo", service.contarEstoqueBaixo());
        model.addAttribute("valorEstoque", service.calcularValorEstoque());
        model.addAttribute("busca", busca);
        model.addAttribute("categoriaSelecionada", categoriaEnum);

        // Para o Modal
        model.addAttribute("novoProduto", new Produto());
        model.addAttribute("categorias", CategoriaProduto.values());
        model.addAttribute("unidades", UnidadeMedida.values());

        // Para o Modal de Venda
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("formasPagamento", FormaPagamento.values());

        return "produto/list";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto, RedirectAttributes redirectAttributes) {
        boolean novo = produto.getId() == null;
        service.salvar(produto);
        redirectAttributes.addFlashAttribute("produtoSucesso", novo ? "cadastrado" : "atualizado");
        redirectAttributes.addFlashAttribute("produtoNome", produto.getNome());
        return "redirect:/produtos";
    }

    @PostMapping("/scan")
    public String scan(@RequestParam("ean") String ean,
                       @RequestParam(value = "quantidade", defaultValue = "1") Integer quantidade,
                       RedirectAttributes redirectAttributes) {
        try {
            var opt = service.buscarPorEan(ean);
            if (opt.isPresent()) {
                var atualizado = service.incrementarEstoquePorEan(ean, quantidade != null ? quantidade : 1);
                redirectAttributes.addFlashAttribute("produtoSucesso", "estoque_incrementado");
                redirectAttributes.addFlashAttribute("produtoNome", atualizado.getNome());
                redirectAttributes.addFlashAttribute("produtoEstoque", atualizado.getEstoque());
            } else {
                redirectAttributes.addFlashAttribute("produtoEan", ean);
                redirectAttributes.addFlashAttribute("produtoScanNovo", true);
            }
        } catch (IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("produtoErro", ex.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/registrar-nota")
    public String registrarNota(@RequestParam("numero") String numero,
                                @RequestParam("valor") java.math.BigDecimal valor,
                                RedirectAttributes redirectAttributes) {
        try {
            var empresa = usuarioService.getUsuarioLogado().getEmpresa();
            LancamentoFinanceiro lanc = new LancamentoFinanceiro();
            lanc.setTipo(TipoLancamento.SAIDA);
            lanc.setCategoria("Compra de Estoque");
            lanc.setDescricao("Nota Fiscal: " + (numero != null ? numero.trim() : "N/D"));
            lanc.setValor(valor != null ? valor : java.math.BigDecimal.ZERO);
            lanc.setData(java.time.LocalDateTime.now());
            financeiroService.salvarLancamento(lanc, empresa);
            redirectAttributes.addFlashAttribute("produtoSucesso", "nota_registrada");
            redirectAttributes.addFlashAttribute("notaNumero", numero);
            redirectAttributes.addFlashAttribute("notaValor", valor);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("produtoErro", "Falha ao registrar nota: " + e.getMessage());
        }
        return "redirect:/produtos";
    }
}
