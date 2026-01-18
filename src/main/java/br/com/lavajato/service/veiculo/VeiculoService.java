package br.com.lavajato.service.veiculo;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.model.veiculo.Veiculo;
import br.com.lavajato.repository.veiculo.VeiculoRepository;
import br.com.lavajato.service.cliente.ClienteService;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteService clienteService;

    public Page<Veiculo> listarPaginado(Pageable pageable, String busca) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        
        if (busca == null || busca.trim().isEmpty()) {
            if (usuario.getPerfil() == Perfil.MASTER) {
                return veiculoRepository.findAll(pageable);
            } else {
                return veiculoRepository.findAllByClienteEmpresa(usuario.getEmpresa(), pageable);
            }
        } else {
            if (usuario.getPerfil() == Perfil.MASTER) {
                return veiculoRepository.buscarTodos(busca, pageable);
            } else {
                return veiculoRepository.buscarPorEmpresa(usuario.getEmpresa(), busca, pageable);
            }
        }
    }

    public Page<Veiculo> listarPaginado(Pageable pageable) {
        return listarPaginado(pageable, null);
    }

    public List<Veiculo> listarTodos() {
        return listarPaginado(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    public Veiculo salvar(Veiculo veiculo) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (veiculo.getCliente() == null || veiculo.getCliente().getId() == null) {
            throw new IllegalStateException("Cliente é obrigatório para o veículo");
        }

        Cliente cliente;
        if (usuario.getPerfil() == Perfil.MASTER) {
            cliente = clienteService.buscarPorId(veiculo.getCliente().getId())
                    .orElseThrow(() -> new IllegalStateException("Cliente não encontrado"));
        } else {
            cliente = clienteService.buscarPorId(veiculo.getCliente().getId())
                    .orElseThrow(() -> new IllegalStateException("Usuário não pode vincular veículo a cliente de outra empresa"));
        }

        veiculo.setCliente(cliente);
        
        // Padronizar placa para maiúsculo
        if (veiculo.getPlaca() != null) {
            veiculo.setPlaca(veiculo.getPlaca().toUpperCase());
        }
        
        return veiculoRepository.save(veiculo);
    }

    public Optional<Veiculo> buscarPorId(Long id) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.getPerfil() == Perfil.MASTER) {
            return veiculoRepository.findById(id);
        } else {
            return veiculoRepository.findByIdAndClienteEmpresa(id, usuario.getEmpresa());
        }
    }
    
    public void excluir(Long id) {
        buscarPorId(id).ifPresent(veiculoRepository::delete);
    }

    public long contarTodos() {
        return veiculoRepository.count();
    }

    public long contarPorEmpresa(Empresa empresa) {
        return veiculoRepository.countByClienteEmpresa(empresa);
    }
}
