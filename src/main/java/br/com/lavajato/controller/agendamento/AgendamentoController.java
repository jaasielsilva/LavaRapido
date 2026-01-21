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
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
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
    public String listar(@PageableDefault(sort = "data", direction = Sort.Direction.ASC, size = 20) Pageable pageable,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
                         @RequestParam(required = false) StatusAgendamento status,
                         @RequestParam(required = false) String busca,
                         Model model) {
        
        // Padrão: Se não houver filtros, assume o dia de hoje (Foco Operacional)
        // Exceto se o usuário limpar explicitamente (mas por enquanto, vamos assumir que null = hoje no carregamento inicial)
        // Para permitir "Ver Tudo", o usuário teria que selecionar datas muito amplas, ou podemos adicionar um flag.
        // Mas para simplificar e atender "dia a dia", default = hoje.
        if (dataInicio == null && dataFim == null && status == null && busca == null) {
            dataInicio = LocalDate.now();
            dataFim = LocalDate.now();
        }

        model.addAttribute("agendamentos", agendamentoService.filtrar(dataInicio, dataFim, status, busca, pageable));
        model.addAttribute("resumo", agendamentoService.obterResumoDoDia());
        
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("status", status);
        model.addAttribute("busca", busca);
        model.addAttribute("todosStatus", StatusAgendamento.values());
        
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
                         @RequestParam(name = "veiculoId", required = false) Long veiculoId,
                         @RequestParam(name = "servicoId", required = false) Long servicoId,
                         org.springframework.validation.BindingResult result,
                         RedirectAttributes redirectAttributes) {
        
        // Vincular Veículo Manualmente
        if (veiculoId != null) {
            veiculoService.buscarPorId(veiculoId).ifPresent(agendamento::setVeiculo);
        }

        // Validação Manual básica se necessário (pois o BindingResult pode ter erros no campo 'veiculo' que era nulo antes)
        if (agendamento.getVeiculo() == null) {
             // Se ainda for nulo, é erro
             redirectAttributes.addFlashAttribute("erro", "Veículo é obrigatório.");
             return "redirect:/agendamentos/novo";
        }

        if (servicoId != null) {
            servicoCatalogoService.buscarPorId(servicoId).ifPresent(servico -> {
                agendamento.setServicos(servico.getNome());
                agendamento.setValor(servico.getPreco());
            });
        }
        
        try {
            agendamentoService.salvar(agendamento);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar agendamento: " + e.getMessage());
            return "redirect:/agendamentos/novo";
        }
        
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
