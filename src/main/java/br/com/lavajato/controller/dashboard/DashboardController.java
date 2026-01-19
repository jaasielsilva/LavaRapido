package br.com.lavajato.controller.dashboard;

import br.com.lavajato.config.SecurityConfig;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.empresa.EmpresaService;
import br.com.lavajato.service.usuario.UsuarioService;
import br.com.lavajato.service.veiculo.VeiculoService;
import br.com.lavajato.service.agendamento.AgendamentoService;
import br.com.lavajato.service.servico.ServicoAvulsoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private ServicoAvulsoService servicoAvulsoService;

    @Autowired
    private SecurityConfig.ActiveSessionCounter activeSessionCounter;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "empresaId", required = false) Long empresaId,
                            Model model) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        model.addAttribute("usuario", usuario);

        boolean isMaster = usuario != null && usuario.isMaster();
        model.addAttribute("isMaster", isMaster);

        // Modo Dashboard Global (Apenas Master sem empresa selecionada explicitamente, mas se for Master e não tiver param, assume Global)
        // Se Master quiser ver sua própria empresa (HQ), poderia ser outro fluxo, mas vamos assumir:
        // Master sem param -> Global Dashboard
        // Master com param -> Tenant Dashboard (Impersonate)
        // Non-Master -> Tenant Dashboard

        boolean showGlobalDashboard = isMaster && empresaId == null;
        model.addAttribute("showGlobalDashboard", showGlobalDashboard);

        if (showGlobalDashboard) {
            // Métricas Globais
            long empresasAtivas = empresaService.contarAtivas();
            long totalVeiculos = veiculoService.contarTodos();
            int usuariosOnline = activeSessionCounter.getActiveSessions();

            model.addAttribute("empresasAtivas", empresasAtivas);
            model.addAttribute("totalVeiculos", totalVeiculos);
            model.addAttribute("usuariosOnline", usuariosOnline);
            model.addAttribute("empresas", empresaService.listarTodas());
        } else {
            // Métricas Operacionais (Tenant/Empresa)
            Empresa empresaContexto;
            
            if (empresaId != null && isMaster) {
                empresaContexto = empresaService.buscarPorId(empresaId)
                        .orElseThrow(() -> new IllegalArgumentException("Empresa inválida: " + empresaId));
            } else {
                empresaContexto = usuario.getEmpresa();
            }
            
            model.addAttribute("empresaContexto", empresaContexto);
            
            // Dados para o Dashboard Operacional (Original)
            long clientesCadastrados = clienteService.contarPorEmpresa(empresaContexto);
            model.addAttribute("clientesCadastrados", clientesCadastrados);

            long agendamentosPendentes = agendamentoService.contarPendentesPorEmpresa(empresaContexto);
            model.addAttribute("agendamentosPendentes", agendamentosPendentes);

            long servicosHoje = servicoAvulsoService.contarConcluidosHoje(empresaContexto);
            model.addAttribute("servicosHoje", servicosHoje);

            java.util.List<br.com.lavajato.model.servico.ServicoAvulso> servicosHojeLista =
                    servicoAvulsoService.listarServicosDoDia(empresaContexto, 5);
            model.addAttribute("servicosHojeLista", servicosHojeLista);

            java.math.BigDecimal faturamentoHoje = servicoAvulsoService.calcularFaturamentoHoje(empresaContexto);
            model.addAttribute("faturamentoHoje", faturamentoHoje);

            java.math.BigDecimal faturamentoVariacao = servicoAvulsoService.calcularVariacaoHojeVsOntem(empresaContexto);
            model.addAttribute("faturamentoVariacao", faturamentoVariacao);
            model.addAttribute("faturamentoSubiu", faturamentoVariacao.compareTo(java.math.BigDecimal.ZERO) > 0);
            model.addAttribute("faturamentoCaiu", faturamentoVariacao.compareTo(java.math.BigDecimal.ZERO) < 0);
            model.addAttribute("faturamentoVariacaoAbsoluta", faturamentoVariacao.abs());

            // Próximos Agendamentos
            java.util.List<br.com.lavajato.model.agendamento.Agendamento> proximosAgendamentos = 
                    agendamentoService.listarProximos(empresaContexto, 5);
            model.addAttribute("proximosAgendamentos", proximosAgendamentos);
        }

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
