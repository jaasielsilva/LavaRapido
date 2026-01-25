package br.com.lavajato.controller.produto;

import br.com.lavajato.model.produto.CategoriaProduto;
import br.com.lavajato.model.produto.Produto;
import br.com.lavajato.model.produto.UnidadeMedida;
import br.com.lavajato.model.venda.FormaPagamento;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.produto.ProdutoService;
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
}
