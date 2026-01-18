package br.com.lavajato.controller.agendamento;

import br.com.lavajato.model.agendamento.Agendamento;
import br.com.lavajato.model.agendamento.StatusAgendamento;
import br.com.lavajato.service.agendamento.AgendamentoService;
import br.com.lavajato.service.veiculo.VeiculoService;
import br.com.lavajato.service.servico.ServicoCatalogoService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ServicoCatalogoService servicoCatalogoService;

    @GetMapping
    public String listar(@PageableDefault(sort = "data", direction = Sort.Direction.ASC) Pageable pageable, Model model) {
        model.addAttribute("agendamentos", agendamentoService.listarPaginado(pageable));
        return "agendamento/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        model.addAttribute("veiculos", veiculoService.listarTodos()); // ListarTodos já filtra por empresa no Service
        model.addAttribute("catalogoServicos", servicoCatalogoService.listarAtivos());
        return "agendamento/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Agendamento agendamento,
                         @RequestParam(name = "servicoId", required = false) Long servicoId) {
        if (servicoId != null) {
            servicoCatalogoService.buscarPorId(servicoId).ifPresent(servico -> {
                agendamento.setServicos(servico.getNome());
                agendamento.setValor(servico.getPreco());
            });
        }
        agendamentoService.salvar(agendamento);
        return "redirect:/agendamentos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Agendamento agendamento = agendamentoService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Agendamento inválido: " + id));
        model.addAttribute("agendamento", agendamento);
        model.addAttribute("veiculos", veiculoService.listarTodos());
        model.addAttribute("catalogoServicos", servicoCatalogoService.listarAtivos());
        return "agendamento/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        br.com.lavajato.model.usuario.Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        try {
            if (usuarioLogado.getPerfil() == br.com.lavajato.model.usuario.Perfil.FUNCIONARIO) {
                throw new IllegalStateException("Você não tem permissão para excluir este registro.");
            }
            agendamentoService.excluir(id);
            redirectAttributes.addFlashAttribute("agendamentoSucesso", "excluido");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("agendamentoErro", e.getMessage());
        }
        return "redirect:/agendamentos";
    }

    @GetMapping("/status/{id}/{status}")
    public String alterarStatus(@PathVariable Long id, @PathVariable StatusAgendamento status) {
        agendamentoService.alterarStatus(id, status);
        return "redirect:/agendamentos";
    }
}
