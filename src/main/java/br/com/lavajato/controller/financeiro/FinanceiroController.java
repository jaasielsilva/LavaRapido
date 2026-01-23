package br.com.lavajato.controller.financeiro;

import br.com.lavajato.dto.MovimentacaoDTO;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import br.com.lavajato.model.financeiro.TipoLancamento;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.service.financeiro.FinanceiroService;
import br.com.lavajato.service.usuario.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private br.com.lavajato.service.EmailService emailService;

    @GetMapping
    public String listar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                         @RequestParam(required = false) Integer periodo,
                         @RequestParam(required = false, defaultValue = "TODOS") String tipo,
                         Model model) {
        
        Usuario usuario = usuarioService.getUsuarioLogado();
        Empresa empresa = usuario.getEmpresa();

        // Lógica de Período (Sobrescreve datas se fornecido)
        if (periodo != null) {
            inicio = LocalDate.now().minusMonths(periodo - 1).with(TemporalAdjusters.firstDayOfMonth());
            fim = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        } else {
            // Default se nada for informado: Últimos 12 meses
            if (inicio == null) {
                periodo = 12; // Define padrão para a UI
                inicio = LocalDate.now().minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
            }
            if (fim == null) {
                fim = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
            }
        }

        // Dados para o Gráfico/Tabela (Balanço Mensal)
        List<br.com.lavajato.dto.BalancoMensalDTO> balancoMensal = financeiroService.gerarBalancoMensal(empresa, inicio, fim, tipo);
        model.addAttribute("balancoMensal", balancoMensal);
        
        // Calcular Total do Balanço para a linha TOTAL
        br.com.lavajato.dto.BalancoMensalDTO totalBalanco = br.com.lavajato.dto.BalancoMensalDTO.builder()
            .receitaServicos(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getReceitaServicos).reduce(BigDecimal.ZERO, BigDecimal::add))
            .receitaAgendamentos(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getReceitaAgendamentos).reduce(BigDecimal.ZERO, BigDecimal::add))
            .receitaProdutos(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getReceitaProdutos).reduce(BigDecimal.ZERO, BigDecimal::add))
            .receitaTotal(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getReceitaTotal).reduce(BigDecimal.ZERO, BigDecimal::add))
            .custosProdutos(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getCustosProdutos).reduce(BigDecimal.ZERO, BigDecimal::add))
            .despesasOperacionais(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getDespesasOperacionais).reduce(BigDecimal.ZERO, BigDecimal::add))
            .lucroLiquido(balancoMensal.stream().map(br.com.lavajato.dto.BalancoMensalDTO::getLucroLiquido).reduce(BigDecimal.ZERO, BigDecimal::add))
            .build();
        model.addAttribute("totalBalanco", totalBalanco);

        // Dados para os Cards (Resumo do Período)
        java.util.Map<String, Object> resumo = financeiroService.calcularResumoFinanceiro(empresa, inicio, fim, tipo);
        model.addAllAttributes(resumo);

        model.addAttribute("inicio", inicio);
        model.addAttribute("fim", fim);
        model.addAttribute("periodo", periodo);
        model.addAttribute("tipo", tipo);
        model.addAttribute("novoLancamento", new LancamentoFinanceiro());

        return "financeiro/list";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute LancamentoFinanceiro lancamento, 
                         @RequestParam(required = false) Long funcionarioId) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        financeiroService.salvarLancamento(lancamento, usuarioLogado.getEmpresa());
        
        // Envio de Comprovante por E-mail (Se for pagamento de funcionário)
        if (funcionarioId != null) {
            usuarioService.buscarPorId(funcionarioId).ifPresent(funcionario -> {
                if (funcionario.getEmail() != null && !funcionario.getEmail().isEmpty()) {
                    emailService.enviarComprovantePagamento(
                        funcionario.getEmail(), 
                        funcionario.getNome(), 
                        lancamento.getValor(), 
                        lancamento.getData(), 
                        lancamento.getDescricao()
                    );
                }
            });
        }
        
        return "redirect:/financeiro";
    }

    @GetMapping("/exportar")
    public void exportar(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
                         @RequestParam(required = false, defaultValue = "TODOS") String tipo,
                         HttpServletResponse response) throws IOException {
        
        Usuario usuario = usuarioService.getUsuarioLogado();
        Empresa empresa = usuario.getEmpresa();
        
        if (inicio == null) inicio = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        if (fim == null) fim = LocalDate.now();

        List<MovimentacaoDTO> movimentacoes = financeiroService.buscarMovimentacoes(empresa, inicio, fim, tipo);
        financeiroService.exportarExcel(movimentacoes, response);
    }
}
