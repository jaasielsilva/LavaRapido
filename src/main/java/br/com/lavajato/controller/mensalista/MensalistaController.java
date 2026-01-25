package br.com.lavajato.controller.mensalista;

import br.com.lavajato.model.mensalista.Mensalista;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.mensalista.MensalistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mensalistas")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'MASTER')")
public class MensalistaController {

    private final MensalistaService mensalistaService;
    private final ClienteService clienteService;

    @GetMapping
    public String listar(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(required = false) String busca,
                         Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataCadastro").descending());
        Page<Mensalista> mensalistas = mensalistaService.listarPaginado(pageable, busca);
        
        model.addAttribute("mensalistas", mensalistas);
        model.addAttribute("busca", busca);
        model.addAttribute("resumo", mensalistaService.obterResumo());
        return "mensalista/list";
    }

    @GetMapping("/fragment")
    public String fragment(@RequestParam(required = false) String busca, Model model) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataCadastro").descending());
        Page<Mensalista> mensalistas = mensalistaService.listarPaginado(pageable, busca);
        model.addAttribute("mensalistas", mensalistas);
        model.addAttribute("busca", busca);
        return "mensalista/list :: listaMensalistas";
    }

    @GetMapping("/suggest")
    @ResponseBody
    public List<br.com.lavajato.dto.MensalistaSugestaoDTO> suggest(@RequestParam String busca) {
        try {
            Page<Mensalista> page = mensalistaService.listarPaginado(PageRequest.of(0, 5, Sort.by("dataCadastro").descending()), busca);
            YearMonth agora = YearMonth.now();
            return page.getContent().stream().map(m -> {
                String status;
                if (!m.getAtivo()) {
                    status = "INATIVO";
                } else if (m.getDataUltimoPagamento() != null &&
                        YearMonth.from(m.getDataUltimoPagamento()).equals(agora)) {
                    status = "EM_DIA";
                } else {
                    status = "PENDENTE";
                }
                return new br.com.lavajato.dto.MensalistaSugestaoDTO(
                        m.getId(),
                        m.getCliente() != null ? m.getCliente().getNome() : "Cliente",
                        m.getCliente() != null ? m.getCliente().getTelefone() : null,
                        m.getDiaVencimento(),
                        m.getValorMensal(),
                        status
                );
            }).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of();
        }
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("mensalista", new Mensalista());
        model.addAttribute("clientes", clienteService.listarTodos());
        return "mensalista/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Mensalista mensalista, RedirectAttributes redirectAttributes) {
        try {
            mensalistaService.salvar(mensalista);
            redirectAttributes.addFlashAttribute("mensalistaSucesso", "salvo");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensalistaErro", "Erro ao salvar: " + e.getMessage());
        }
        return "redirect:/mensalistas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("mensalista", mensalistaService.buscarPorId(id));
        model.addAttribute("clientes", clienteService.listarTodos());
        return "mensalista/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            mensalistaService.excluir(id);
            redirectAttributes.addFlashAttribute("mensalistaSucesso", "excluido");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensalistaErro", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/mensalistas";
    }

    @PostMapping("/pagar")
    public String registrarPagamento(@RequestParam Long mensalistaId,
                                     @RequestParam BigDecimal valor,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento,
                                     RedirectAttributes redirectAttributes) {
        try {
            mensalistaService.registrarPagamento(mensalistaId, valor, dataPagamento);
            redirectAttributes.addFlashAttribute("mensalistaSucesso", "pagamento");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensalistaErro", "Erro ao registrar pagamento: " + e.getMessage());
        }
        return "redirect:/mensalistas";
    }
}
