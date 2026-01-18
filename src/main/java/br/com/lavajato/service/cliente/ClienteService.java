package br.com.lavajato.service.cliente;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.cliente.ClienteRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioService usuarioService;

    public Page<Cliente> listarPaginado(Pageable pageable) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.getPerfil() == Perfil.MASTER) {
            return clienteRepository.findAll(pageable);
        } else {
            return clienteRepository.findAllByEmpresa(usuario.getEmpresa(), pageable);
        }
    }

    public List<Cliente> listarTodos() {
        return listarPaginado(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    public Cliente salvar(Cliente cliente) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        
        if (cliente.getId() == null) {
            cliente.setDataCadastro(LocalDate.now());
        }
        
        if (usuario.getPerfil() != Perfil.MASTER) {
            cliente.setEmpresa(usuario.getEmpresa());
        }
        // Se for Master, a empresa deve vir selecionada no formulário (ou tratar erro)
        return clienteRepository.save(cliente);
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.getPerfil() == Perfil.MASTER) {
            return clienteRepository.findById(id);
        } else {
            return clienteRepository.findByIdAndEmpresa(id, usuario.getEmpresa());
        }
    }

    public void excluir(Long id) {
        buscarPorId(id).ifPresent(clienteRepository::delete);
    }

    public long contarPorEmpresa(Empresa empresa) {
        return clienteRepository.countByEmpresa(empresa);
    }
}
