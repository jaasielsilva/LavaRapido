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
import java.time.Period;
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
        return clienteRepository.save(cliente);
    }
    
    public Optional<Cliente> buscarPorId(Long id) {
        Usuario usuario = usuarioService.getUsuarioLogado();
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
        if (cliente == null) return;

        LocalDate hoje = LocalDate.now();

        // 1. Início de Ciclo (Primeira lavagem)
        if (cliente.getQuantidadeLavagens() == 0) {
            cliente.setDataInicioFidelidade(hoje);
            cliente.setQuantidadeLavagens(1);
        } 
        else {
            // 2. Verificar Validade do Ciclo (4 meses)
            LocalDate inicio = cliente.getDataInicioFidelidade();
            if (inicio == null) {
                // Caso legado ou inconsistente, assume hoje como início
                inicio = hoje;
                cliente.setDataInicioFidelidade(inicio);
            }

            Period periodo = Period.between(inicio, hoje);
            long mesesPassados = periodo.toTotalMonths();

            if (mesesPassados > 4) {
                // Expirou o ciclo: Zera e começa novo com a lavagem atual
                cliente.setQuantidadeLavagens(1);
                cliente.setDataInicioFidelidade(hoje);
            } else {
                // Dentro do prazo: Acumula
                cliente.setQuantidadeLavagens(cliente.getQuantidadeLavagens() + 1);
            }
        }
        
        clienteRepository.save(cliente);
    }

    public void resgatarFidelidade(Cliente cliente) {
        if (cliente != null && cliente.getQuantidadeLavagens() >= 10) {
            cliente.setQuantidadeLavagens(cliente.getQuantidadeLavagens() - 10);
            
            // Reinicia o ciclo para as próximas lavagens
            // Isso garante que o cliente tenha mais 4 meses para juntar as próximas 10
            cliente.setDataInicioFidelidade(LocalDate.now());
            
            clienteRepository.save(cliente);
        }
    }
}
