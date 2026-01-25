package br.com.lavajato.service.servico;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.servico.ServicoAvulsoRepository;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ServicoAvulsoService {

    @Autowired
    private ServicoAvulsoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    public List<ServicoAvulso> listarTodos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.findAllByEmpresaOrderByDataCriacaoDesc(usuario.getEmpresa());
    }

    public Page<ServicoAvulso> listarPaginado(Pageable pageable) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.findAllByEmpresaOrderByDataCriacaoDesc(usuario.getEmpresa(), pageable);
    }
    
    // Manter compatibilidade se necessário, mas o ideal é usar o de cima
    public Page<ServicoAvulso> listarPaginado(int page, int size) {
        return listarPaginado(PageRequest.of(page, size, Sort.by("dataCriacao").descending()));
    }

    public ServicoAvulso buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
    }

    public void excluir(Long id) {
        ServicoAvulso servico = buscarPorId(id);
        repository.delete(servico);
    }

    public void concluirServico(Long id) {
        alterarStatus(id, StatusServico.CONCLUIDO);
    }

    public ServicoAvulso salvar(ServicoAvulso servicoAvulso) {
        if (servicoAvulso.getCliente() == null &&
                (servicoAvulso.getClienteAvulsoNome() == null || servicoAvulso.getClienteAvulsoNome().isBlank())) {
            throw new IllegalStateException("É obrigatório informar um cliente (cadastrado ou avulso).");
        }

        Usuario usuario = usuarioService.getUsuarioLogado();
        servicoAvulso.setEmpresa(usuario.getEmpresa());

        // Regra de Segurança: FUNCIONARIO não pode alterar preço de serviço tabelado
        if (usuario.getPerfil() == br.com.lavajato.model.usuario.Perfil.FUNCIONARIO && servicoAvulso.getServico() != null) {
            servicoAvulso.setValor(servicoAvulso.getServico().getPreco());
        }
        
        // Lógica de Fidelidade (Resgate)
        if (Boolean.TRUE.equals(servicoAvulso.getUsoFidelidade())) {
            servicoAvulso.setValor(BigDecimal.ZERO);
            
            if (servicoAvulso.getId() == null) {
                if (servicoAvulso.getCliente() != null) {
                    clienteService.resgatarFidelidade(servicoAvulso.getCliente());
                }
            } else {
                java.util.Optional<ServicoAvulso> anterior = repository.findById(servicoAvulso.getId());
                if (anterior.isPresent() && !Boolean.TRUE.equals(anterior.get().getUsoFidelidade())) {
                    if (servicoAvulso.getCliente() != null) {
                        clienteService.resgatarFidelidade(servicoAvulso.getCliente());
                    }
                }
            }
        }
        
        // Define preço e nome baseados no serviço do catálogo, se não vierem
        if (servicoAvulso.getServico() != null) {
            if (servicoAvulso.getValor() == null) {
                servicoAvulso.setValor(servicoAvulso.getServico().getPreco());
            }
            if (servicoAvulso.getNomeServicoHistorico() == null) {
                servicoAvulso.setNomeServicoHistorico(servicoAvulso.getServico().getNome());
            }
        }
        
        ServicoAvulso salvo = repository.save(servicoAvulso);
        if (salvo.getProtocolo() == null || salvo.getProtocolo().isBlank()) {
            String codigo = String.format("PROC-%03d", salvo.getId());
            salvo.setProtocolo(codigo);
            salvo = repository.save(salvo);
        }
        return salvo;
    }
    
    public void alterarStatus(Long id, StatusServico novoStatus) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        repository.findById(id).ifPresent(s -> {
            if (s.getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
                StatusServico statusAnterior = s.getStatus();
                s.setStatus(novoStatus);
                if (novoStatus == StatusServico.EM_ANDAMENTO && s.getDataInicio() == null) {
                    s.setDataInicio(LocalDateTime.now());
                }
                if (novoStatus == StatusServico.CONCLUIDO) {
                    s.setDataConclusao(LocalDateTime.now());
                    if (s.getDataInicio() == null) {
                        s.setDataInicio(s.getDataCriacao());
                    }
                    
                    // Fidelidade Incremento
                    if (statusAnterior != StatusServico.CONCLUIDO) {
                         if (!Boolean.TRUE.equals(s.getUsoFidelidade()) && s.getCliente() != null) {
                             clienteService.incrementarFidelidade(s.getCliente());
                         }
                    }
                }
                repository.save(s);
            }
        });
    }

    // Métricas
    public long contarEmFila() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.countByEmpresaAndStatus(usuario.getEmpresa(), StatusServico.EM_FILA);
    }

    public long contarConcluidosHoje() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return contarConcluidosHoje(usuario.getEmpresa());
    }

    public BigDecimal calcularFaturamentoHoje() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return calcularFaturamentoHoje(usuario.getEmpresa());
    }

    public long contarConcluidosHoje(Empresa empresa) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        return repository.countConcluidosHoje(empresa, inicio, fim);
    }

    public BigDecimal calcularFaturamentoHoje(Empresa empresa) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal valor = repository.sumFaturamentoHoje(empresa, inicio, fim);
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public java.util.List<ServicoAvulso> listarServicosDoDia(Empresa empresa, int limite) {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        java.util.List<ServicoAvulso> lista = repository.findServicosDoDia(empresa, inicio, fim);
        if (lista.size() > limite) {
            return lista.subList(0, limite);
        }
        return lista;
    }

    public BigDecimal calcularFaturamentoOntem(Empresa empresa) {
        LocalDate ontem = LocalDate.now().minusDays(1);
        LocalDateTime inicio = ontem.atStartOfDay();
        LocalDateTime fim = ontem.atTime(LocalTime.MAX);
        BigDecimal valor = repository.sumFaturamentoHoje(empresa, inicio, fim);
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public BigDecimal calcularVariacaoHojeVsOntem(Empresa empresa) {
        BigDecimal hoje = calcularFaturamentoHoje(empresa);
        BigDecimal ontem = calcularFaturamentoOntem(empresa);

        if (ontem.compareTo(BigDecimal.ZERO) == 0) {
            if (hoje.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(100);
        }

        BigDecimal diferenca = hoje.subtract(ontem);
        return diferenca
                .multiply(BigDecimal.valueOf(100))
                .divide(ontem, 2, RoundingMode.HALF_UP);
    }
}
