package br.com.lavajato.service.servico;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.CategoriaServico;
import br.com.lavajato.model.servico.Servico;
import br.com.lavajato.model.servico.TipoServico;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.servico.ServicoRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServicoCatalogoService {

    @Autowired
    private ServicoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    public List<Servico> listarAtivos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.findAllByEmpresaAndAtivoTrue(usuario.getEmpresa());
    }
    
    public List<Servico> listarTodos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        return repository.findAllByEmpresa(usuario.getEmpresa());
    }

    public Servico salvar(Servico servico) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        servico.setEmpresa(usuario.getEmpresa());
        return repository.save(servico);
    }
    
    public Optional<Servico> buscarPorId(Long id) {
        // TODO: Validar se pertence a empresa
        return repository.findById(id);
    }

    public void inicializarServicosPadrao(Empresa empresa) {
        if (repository.findAllByEmpresa(empresa).isEmpty()) {
            for (TipoServico tipo : TipoServico.values()) {
                Servico s = new Servico();
                s.setEmpresa(empresa);
                s.setNome(tipo.getDescricao());
                s.setPreco(tipo.getPreco());
                s.setTempoMinutos(tipo.getTempoMinutos());
                s.setAtivo(true);
                
                // Categorização Automática Simples
                String nomeUpper = tipo.getDescricao().toUpperCase();
                if (nomeUpper.contains("LAVAGEM")) {
                    s.setCategoria(CategoriaServico.LAVAGEM);
                    s.setDescricao(nomeUpper.contains("SIMPLES") ? "Lavagem externa completa" : "Lavagem completa + acabamento");
                } else if (nomeUpper.contains("POLIMENTO")) {
                    s.setCategoria(CategoriaServico.POLIMENTO);
                    s.setDescricao("Polimento com correção de pintura");
                } else if (nomeUpper.contains("CRISTALIZACAO") || nomeUpper.contains("VITRIFICACAO") || nomeUpper.contains("HIDRATACAO")) {
                    s.setCategoria(CategoriaServico.ESTETICA);
                    s.setDescricao("Tratamento estético avançado");
                } else {
                    s.setCategoria(CategoriaServico.OUTROS);
                    s.setDescricao("Serviço especializado");
                }
                
                repository.save(s);
            }
        }
    }
}
