package br.com.lavajato.repository.usuario;

import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.model.empresa.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findAllByEmpresa(Empresa empresa);
    Page<Usuario> findAllByEmpresa(Empresa empresa, Pageable pageable);
    long countByEmpresa(Empresa empresa);
}
