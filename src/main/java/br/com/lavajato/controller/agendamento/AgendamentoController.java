package br.com.lavajato.controller.agendamento;

import br.com.lavajato.model.agendamento.Agendamento;
import br.com.lavajato.model.agendamento.StatusAgendamento;
import br.com.lavajato.service.agendamento.AgendamentoService;
import br.com.lavajato.service.veiculo.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping
    public String listar(@PageableDefault(sort = "data", direction = Sort.Direction.ASC) Pageable pageable, Model model) {
        model.addAttribute("agendamentos", agendamentoService.listarPaginado(pageable));
        return "agendamento/list";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        model.addAttribute("veiculos", veiculoService.listarTodos()); // ListarTodos já filtra por empresa no Service
        return "agendamento/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Agendamento agendamento) {
        agendamentoService.salvar(agendamento);
        return "redirect:/agendamentos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Agendamento agendamento = agendamentoService.buscarPorId(id).orElseThrow(() -> new IllegalArgumentException("Agendamento inválido: " + id));
        model.addAttribute("agendamento", agendamento);
        model.addAttribute("veiculos", veiculoService.listarTodos());
        return "agendamento/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        agendamentoService.excluir(id);
        return "redirect:/agendamentos";
    }

    @GetMapping("/status/{id}/{status}")
    public String alterarStatus(@PathVariable Long id, @PathVariable StatusAgendamento status) {
        agendamentoService.alterarStatus(id, status);
        return "redirect:/agendamentos";
    }
}
