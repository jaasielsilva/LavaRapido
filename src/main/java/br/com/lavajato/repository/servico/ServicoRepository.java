package br.com.lavajato.repository.servico;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.CategoriaServico;
import br.com.lavajato.model.servico.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {
    List<Servico> findAllByEmpresaAndAtivoTrue(Empresa empresa);
    List<Servico> findAllByEmpresa(Empresa empresa);
    List<Servico> findAllByEmpresaAndCategoria(Empresa empresa, CategoriaServico categoria);
}
