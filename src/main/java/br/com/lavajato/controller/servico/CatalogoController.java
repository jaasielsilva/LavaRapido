package br.com.lavajato.controller.servico;

import br.com.lavajato.model.servico.CategoriaServico;
import br.com.lavajato.model.servico.Servico;
import br.com.lavajato.service.servico.ServicoCatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    @Autowired
    private ServicoCatalogoService catalogoService;

    @GetMapping
    public String listar(Model model) {
        List<Servico> servicos = catalogoService.listarAtivos();
        
        // Agrupar por categoria para facilitar a view
        Map<CategoriaServico, List<Servico>> servicosPorCategoria = servicos.stream()
                .collect(Collectors.groupingBy(Servico::getCategoria));
        
        model.addAttribute("servicosPorCategoria", servicosPorCategoria);
        model.addAttribute("todasCategorias", CategoriaServico.values());
        
        return "catalogo/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("servico", new Servico());
        model.addAttribute("categorias", CategoriaServico.values());
        return "catalogo/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Servico servico) {
        catalogoService.salvar(servico);
        return "redirect:/catalogo";
    }
}
