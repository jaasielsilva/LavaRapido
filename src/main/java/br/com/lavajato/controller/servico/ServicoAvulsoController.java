package br.com.lavajato.controller.servico;

import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.Servico;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.service.servico.ServicoCatalogoService;
import br.com.lavajato.service.servico.ServicoAvulsoService;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/servicos-avulsos")
public class ServicoAvulsoController {

    @Autowired
    private ServicoAvulsoService servicoService;

    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private ServicoCatalogoService catalogoService;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private br.com.lavajato.service.storage.FileStorageService fileStorageService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
        var servicosAtivos = catalogoService.listarAtivos();

        // Métricas
        model.addAttribute("emFila", servicoService.contarEmFila());
        model.addAttribute("concluidosHoje", servicoService.contarConcluidosHoje());
        model.addAttribute("faturamentoHoje", servicoService.calcularFaturamentoHoje());

        // Listagem paginada
        model.addAttribute("servicos", servicoService.listarPaginado(PageRequest.of(page, size, Sort.by("dataCriacao").descending())));
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        
        // Para o Modal
        model.addAttribute("novoServico", new ServicoAvulso());
        // Clientes carregados via AJAX (Select2)
        model.addAttribute("catalogoServicos", servicosAtivos);

        return "servico-avulso/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        var servicosAtivos = catalogoService.listarAtivos();

        model.addAttribute("novoServico", new ServicoAvulso());
        model.addAttribute("catalogoServicos", servicosAtivos);
        return "servico-avulso/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute ServicoAvulso servico,
                         @RequestParam("servico") Long servicoId,
                         @RequestParam(value = "fotos", required = false) org.springframework.web.multipart.MultipartFile[] fotos,
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
            ServicoAvulso salvo = servicoService.salvar(servico);
            // Salvar fotos anexadas, se houver
            try {
                if (fotos != null && fotos.length > 0) {
                    fileStorageService.salvarFotosServico(salvo.getEmpresa(), salvo, fotos);
                }
            } catch (Exception e) {
                // Não impede cadastro da OS; apenas registra que houve erro ao salvar anexos
                System.err.println("Erro ao salvar fotos da OS: " + e.getMessage());
            }
            redirectAttributes.addFlashAttribute("servicoAvulsoSucesso", "cadastrado");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("servicoAvulsoErro", e.getMessage());
        }

        return "redirect:/servicos-avulsos";
    }

    @GetMapping("/status/{id}/{status}")
    public String alterarStatus(@PathVariable Long id, @PathVariable StatusServico status, RedirectAttributes redirectAttributes) {
        servicoService.alterarStatus(id, status);
        if (status == StatusServico.CONCLUIDO) {
            redirectAttributes.addFlashAttribute("conclusaoServicoId", id);
        }
        return "redirect:/servicos-avulsos";
    }
}
