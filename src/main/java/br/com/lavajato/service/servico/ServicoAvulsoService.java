package br.com.lavajato.service.servico;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.servico.ServicoAvulsoRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ServicoAvulsoService {

    @Autowired
    private ServicoAvulsoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    public List<ServicoAvulso> listarTodos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.findAllByEmpresaOrderByDataCriacaoDesc(usuario.getEmpresa());
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
        
        // Define preço e nome baseados no serviço do catálogo, se não vierem
        if (servicoAvulso.getServico() != null) {
            if (servicoAvulso.getValor() == null) {
                servicoAvulso.setValor(servicoAvulso.getServico().getPreco());
            }
            if (servicoAvulso.getNomeServicoHistorico() == null) {
                servicoAvulso.setNomeServicoHistorico(servicoAvulso.getServico().getNome());
            }
        }
        
        return repository.save(servicoAvulso);
    }
    
    public void alterarStatus(Long id, StatusServico novoStatus) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        repository.findById(id).ifPresent(s -> {
            if (s.getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
                s.setStatus(novoStatus);
                if (novoStatus == StatusServico.CONCLUIDO) {
                    s.setDataConclusao(LocalDateTime.now());
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
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        return repository.countConcluidosHoje(usuario.getEmpresa(), inicio, fim);
    }

    public BigDecimal calcularFaturamentoHoje() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDate.now().atTime(LocalTime.MAX);
        return repository.sumFaturamentoHoje(usuario.getEmpresa(), inicio, fim);
    }
}
