package br.com.lavajato.service.mensalista;

import br.com.lavajato.dto.MensalistaResumoDTO;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import br.com.lavajato.model.financeiro.TipoLancamento;
import br.com.lavajato.model.mensalista.Mensalista;
import br.com.lavajato.model.mensalista.PagamentoMensalista;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.financeiro.LancamentoFinanceiroRepository;
import br.com.lavajato.repository.mensalista.MensalistaRepository;
import br.com.lavajato.repository.mensalista.PagamentoMensalistaRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensalistaService {

    private final MensalistaRepository mensalistaRepository;
    private final PagamentoMensalistaRepository pagamentoMensalistaRepository;
    private final LancamentoFinanceiroRepository lancamentoFinanceiroRepository;
    private final UsuarioService usuarioService;

    public List<Mensalista> listarTodos() {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        if (usuarioLogado.isMaster()) {
            return mensalistaRepository.findAll();
        }
        return mensalistaRepository.findByEmpresaAndAtivoTrue(usuarioLogado.getEmpresa());
    }

    public Page<Mensalista> listarPaginado(Pageable pageable, String busca) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        Empresa empresa = usuarioLogado.getEmpresa();
        
        if (busca != null && !busca.trim().isEmpty()) {
            return mensalistaRepository.buscarPorTermo(empresa, busca, pageable);
        }
        
        return mensalistaRepository.findByEmpresaAndAtivoTrue(empresa, pageable);
    }

    public MensalistaResumoDTO obterResumo() {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        Empresa empresa = usuarioLogado.getEmpresa();

        long totalAtivos = mensalistaRepository.countByEmpresaAndAtivoTrue(empresa);
        BigDecimal receitaPrevista = mensalistaRepository.sumValorMensalByEmpresa(empresa);
        
        LocalDate inicioMes = YearMonth.now().atDay(1);
        LocalDate fimMes = YearMonth.now().atEndOfMonth();
        BigDecimal recebidoMes = pagamentoMensalistaRepository.sumValorPagoByEmpresaAndPeriodo(empresa, inicioMes, fimMes);

        return new MensalistaResumoDTO(
            totalAtivos, 
            receitaPrevista != null ? receitaPrevista : BigDecimal.ZERO, 
            recebidoMes != null ? recebidoMes : BigDecimal.ZERO
        );
    }

    public Mensalista buscarPorId(Long id) {
        return mensalistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensalista não encontrado"));
    }

    @Transactional
    public void salvar(Mensalista mensalista) {
        Usuario usuarioLogado = usuarioService.getUsuarioLogado();
        
        // Se for edição, mantém a empresa original, senão define a do usuário logado
        if (mensalista.getId() == null) {
            mensalista.setEmpresa(usuarioLogado.getEmpresa());
            mensalista.setAtivo(true);
        } else {
            Mensalista existente = buscarPorId(mensalista.getId());
            mensalista.setEmpresa(existente.getEmpresa());
            mensalista.setDataCadastro(existente.getDataCadastro());
            mensalista.setAtivo(existente.getAtivo());
            mensalista.setDataUltimoPagamento(existente.getDataUltimoPagamento());
        }
        
        mensalistaRepository.save(mensalista);
    }

    @Transactional
    public void excluir(Long id) {
        Mensalista mensalista = buscarPorId(id);
        mensalista.excluir();
        mensalistaRepository.save(mensalista);
    }

    @Transactional
    public void registrarPagamento(Long mensalistaId, BigDecimal valor, LocalDate dataPagamento) {
        Mensalista mensalista = buscarPorId(mensalistaId);
        Empresa empresa = mensalista.getEmpresa();

        // 1. Criar registro de pagamento histórico
        PagamentoMensalista pagamento = new PagamentoMensalista();
        pagamento.setMensalista(mensalista);
        pagamento.setEmpresa(empresa);
        pagamento.setDataPagamento(dataPagamento);
        pagamento.setValorPago(valor);
        
        // Define competência baseada na data do pagamento (MM/yyyy)
        String competencia = dataPagamento.format(DateTimeFormatter.ofPattern("MM/yyyy"));
        pagamento.setCompetencia(competencia);
        
        pagamentoMensalistaRepository.save(pagamento);

        // 2. Integrar com Financeiro (Lançamento de Entrada)
        LancamentoFinanceiro lancamento = new LancamentoFinanceiro();
        lancamento.setEmpresa(empresa);
        lancamento.setDescricao("Mensalidade - " + mensalista.getCliente().getNome());
        lancamento.setValor(valor);
        lancamento.setData(dataPagamento.atStartOfDay());
        lancamento.setTipo(TipoLancamento.ENTRADA);
        lancamento.setCategoria("Mensalidade");
        
        lancamentoFinanceiroRepository.save(lancamento);

        // 3. Atualizar Mensalista
        mensalista.setDataUltimoPagamento(dataPagamento);
        mensalistaRepository.save(mensalista);
    }
}
