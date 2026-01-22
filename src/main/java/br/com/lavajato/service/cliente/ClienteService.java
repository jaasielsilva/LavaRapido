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

    public Page<Cliente> listarPaginado(Pageable pageable, String busca) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        
        if (busca != null && !busca.trim().isEmpty()) {
            if (usuario.getPerfil() == Perfil.MASTER) {
                // Para Master, busca global (simplificado por enquanto, ou restrito à empresa se houver contexto)
                // Assumindo busca na empresa do master por padrão ou implementar busca global depois
                return clienteRepository.buscarPorTermo(usuario.getEmpresa(), busca, pageable);
            } else {
                return clienteRepository.buscarPorTermo(usuario.getEmpresa(), busca, pageable);
            }
        }

        if (usuario.getPerfil() == Perfil.MASTER) {
            return clienteRepository.findAllByAtivoTrue(pageable);
        } else {
            return clienteRepository.findAllByEmpresaAndAtivoTrue(usuario.getEmpresa(), pageable);
        }
    }

    public List<Cliente> listarTodos() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.getPerfil() == Perfil.MASTER) {
            return clienteRepository.findAllByAtivoTrue();
        } else {
            return clienteRepository.findAllByEmpresaAndAtivoTrue(usuario.getEmpresa());
        }
    }

    public Cliente salvar(Cliente cliente) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        
        if (cliente.getId() == null) {
            cliente.setDataCadastro(LocalDate.now());
            cliente.setAtivo(true);
        }
        
        if (usuario.getPerfil() != Perfil.MASTER) {
            cliente.setEmpresa(usuario.getEmpresa());
        }
        // Se for Master, a empresa deve vir selecionada no formulário (ou tratar erro)
        return clienteRepository.save(cliente);
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        // Permite buscar inativos se necessário, mas por padrão foca na consistência
        // Se for edição, precisamos buscar mesmo se inativo? Geralmente não edita excluido.
        if (usuario.getPerfil() == Perfil.MASTER) {
            return clienteRepository.findById(id).filter(Cliente::isAtivo);
        } else {
            return clienteRepository.findByIdAndEmpresaAndAtivoTrue(id, usuario.getEmpresa());
        }
    }

    public void excluir(Long id) {
        buscarPorId(id).ifPresent(cliente -> {
            cliente.setAtivo(false);
            clienteRepository.save(cliente);
        });
    }

    public long contarPorEmpresa(Empresa empresa) {
        return clienteRepository.countByEmpresaAndAtivoTrue(empresa);
    }

    public void incrementarFidelidade(Cliente cliente) {
        if (cliente != null) {
            cliente.setQuantidadeLavagens(cliente.getQuantidadeLavagens() + 1);
            clienteRepository.save(cliente);
        }
    }

    public void resgatarFidelidade(Cliente cliente) {
        if (cliente != null && cliente.getQuantidadeLavagens() >= 10) {
            cliente.setQuantidadeLavagens(cliente.getQuantidadeLavagens() - 10);
            clienteRepository.save(cliente);
        }
    }
}
