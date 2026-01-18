package br.com.lavajato.repository.empresa;

import br.com.lavajato.model.empresa.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    long countByAtivoTrue();
}
