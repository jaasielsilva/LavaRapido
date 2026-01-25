package br.com.lavajato.repository.produto;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.produto.MovimentoEstoque;
import br.com.lavajato.model.produto.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimentoEstoqueRepository extends JpaRepository<MovimentoEstoque, Long> {
    Page<MovimentoEstoque> findByEmpresaAndProdutoOrderByDataDesc(Empresa empresa, Produto produto, Pageable pageable);
}
