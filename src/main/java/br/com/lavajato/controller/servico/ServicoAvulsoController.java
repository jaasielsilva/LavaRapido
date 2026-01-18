package br.com.lavajato.controller.servico;

import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.service.servico.ServicoCatalogoService;
import br.com.lavajato.service.servico.ServicoAvulsoService;
import br.com.lavajato.service.cliente.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/servicos-avulsos")
public class ServicoAvulsoController {

    @Autowired
    private ServicoAvulsoService servicoService;

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ServicoCatalogoService catalogoService;

    @GetMapping
    public String listar(Model model) {
        // Métricas
        model.addAttribute("emFila", servicoService.contarEmFila());
        model.addAttribute("concluidosHoje", servicoService.contarConcluidosHoje());
        model.addAttribute("faturamentoHoje", servicoService.calcularFaturamentoHoje());

        // Listagem
        model.addAttribute("servicos", servicoService.listarTodos());
        
        // Para o Modal
        model.addAttribute("novoServico", new ServicoAvulso());
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("catalogoServicos", catalogoService.listarAtivos()); // Carrega do banco

        return "servico-avulso/list";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ServicoAvulso servico,
                         @RequestParam("servico") Long servicoId,
                         RedirectAttributes redirectAttributes) {
        catalogoService.buscarPorId(servicoId).ifPresentOrElse(
                servico::setServico,
                () -> {
                    throw new IllegalArgumentException("Serviço selecionado não foi encontrado.");
                }
        );

        if (servico.getCliente() != null && servico.getCliente().getId() != null) {
            servico.setClienteAvulsoNome(null);
            servico.setClienteAvulsoVeiculo(null);
        }

        try {
            servicoService.salvar(servico);
            redirectAttributes.addFlashAttribute("servicoAvulsoSucesso", "cadastrado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("servicoAvulsoErro", e.getMessage());
        }

        return "redirect:/servicos-avulsos";
    }

    @GetMapping("/status/{id}/{status}")
    public String alterarStatus(@PathVariable Long id, @PathVariable StatusServico status) {
        servicoService.alterarStatus(id, status);
        return "redirect:/servicos-avulsos";
    }
}
