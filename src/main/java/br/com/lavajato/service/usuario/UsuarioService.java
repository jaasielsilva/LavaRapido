package br.com.lavajato.service.usuario;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        return usuarioRepository.findByEmail(auth.getName()).orElse(null);
    }

    public List<Usuario> listarPorEmpresa(Empresa empresa) {
        return usuarioRepository.findAllByEmpresa(empresa);
    }

    public Page<Usuario> listarPaginado(Pageable pageable) {
        Usuario usuario = getUsuarioLogado();
        if (usuario.isMaster()) {
            return usuarioRepository.findAll(pageable);
        }
        return usuarioRepository.findAllByEmpresa(usuario.getEmpresa(), pageable);
    }

    public List<Usuario> listarTodos() {
        return listarPaginado(PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    public Usuario salvar(Usuario usuario) {
        // Criptografar senha se for nova ou alterada (lógica simplificada: sempre criptografa se vier preenchida)
        // Em um caso real, verificaríamos se a senha mudou.
        // Aqui assumimos que o form envia a senha crua.
        if (usuario.getSenha() != null && !usuario.getSenha().startsWith("$2a$")) {
             usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuarioInicial(String nome, String email, String senha, Perfil perfil, Empresa empresa) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(perfil);
        usuario.setEmpresa(empresa);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        Usuario usuario = getUsuarioLogado();
        if (usuario.isMaster()) {
            return usuarioRepository.findById(id);
        }
        // Non-master can only see users from their own company
        Optional<Usuario> user = usuarioRepository.findById(id);
        if (user.isPresent() && user.get().getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
            return user;
        }
        return Optional.empty();
    }
    
    public void excluir(Long id) {
        buscarPorId(id).ifPresent(usuarioRepository::delete);
    }

    public long contarPorEmpresa(Empresa empresa) {
        return usuarioRepository.countByEmpresa(empresa);
    }
}
