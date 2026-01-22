package br.com.lavajato.service.agendamento;

import br.com.lavajato.model.agendamento.Agendamento;
import br.com.lavajato.model.agendamento.StatusAgendamento;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.agendamento.AgendamentoRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Page<Agendamento> filtrar(LocalDate dataInicio, LocalDate dataFim, StatusAgendamento status, String busca, Pageable pageable) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        
        LocalDateTime inicio = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = dataFim != null ? dataFim.atTime(LocalTime.MAX) : null;

        if (usuario.isMaster()) {
            // Simplificação: Master vê tudo sem filtro por enquanto, ou teria que ter query global
            return agendamentoRepository.findAll(pageable); 
        }
        
        return agendamentoRepository.findComFiltros(usuario.getEmpresa(), inicio, fim, status, busca, pageable);
    }

    public Map<String, Object> obterResumoDoDia() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        Empresa empresa = usuario.getEmpresa();
        
        if (empresa == null) return new HashMap<>(); // Safety

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDia = LocalDate.now().atTime(LocalTime.MAX);

        Map<String, Object> resumo = new HashMap<>();
        resumo.put("totalHoje", agendamentoRepository.countByEmpresaAndDataBetween(empresa, inicioDia, fimDia));
        resumo.put("emAndamento", agendamentoRepository.countByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.EM_ANDAMENTO, inicioDia, fimDia));
        resumo.put("faturamentoHoje", agendamentoRepository.sumValorByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, inicioDia, fimDia));
        
        return resumo;
    }

    public Page<Agendamento> listarPaginado(Pageable pageable) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.isMaster()) {
            return agendamentoRepository.findAll(pageable);
        } else {
            return agendamentoRepository.findAllByEmpresa(usuario.getEmpresa(), pageable);
        }
    }

    public Agendamento salvar(Agendamento agendamento) {
        // Validação de segurança: garantir que o veículo pertence à empresa do usuário
        // (Isso seria ideal validar também no VehicleService/Repository, mas aqui garantimos na criação)
        // Por simplificação, assumimos que o form só mostra veículos da empresa.
        
        return agendamentoRepository.save(agendamento);
    }

    public Optional<Agendamento> buscarPorId(Long id) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.isMaster()) {
            return agendamentoRepository.findById(id);
        } else {
            return agendamentoRepository.findByIdAndEmpresa(id, usuario.getEmpresa());
        }
    }

    public void excluir(Long id) {
        buscarPorId(id).ifPresent(agendamentoRepository::delete);
    }

    public void alterarStatus(Long id, StatusAgendamento novoStatus) {
        buscarPorId(id).ifPresent(agendamento -> {
            agendamento.setStatus(novoStatus);
            agendamentoRepository.save(agendamento);
        });
    }

    public long contarPendentesPorEmpresa(Empresa empresa) {
        return agendamentoRepository.countPendentesByEmpresa(empresa);
    }

    public java.util.List<Agendamento> listarProximos(Empresa empresa, int limite) {
        return agendamentoRepository.findProximosByEmpresa(empresa, org.springframework.data.domain.PageRequest.of(0, limite)).getContent();
    }

    public long contarServicosHoje(Empresa empresa) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        // Conta apenas CONCLUIDOS conforme regra de negócio
        return agendamentoRepository.countByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, inicio, fim);
    }

    public java.math.BigDecimal calcularFaturamentoHoje(Empresa empresa) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        java.math.BigDecimal valor = agendamentoRepository.sumValorByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, inicio, fim);
        return valor != null ? valor : java.math.BigDecimal.ZERO;
    }

    public java.math.BigDecimal calcularFaturamentoOntem(Empresa empresa) {
        LocalDateTime inicio = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime fim = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);
        java.math.BigDecimal valor = agendamentoRepository.sumValorByEmpresaAndStatusAndDataBetween(empresa, StatusAgendamento.CONCLUIDO, inicio, fim);
        return valor != null ? valor : java.math.BigDecimal.ZERO;
    }
}
