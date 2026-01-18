package br.com.lavajato.repository.produto;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.produto.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByEmpresaAndAtivoTrue(Empresa empresa);
    
    long countByEmpresaAndAtivoTrue(Empresa empresa);
    
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.empresa = :empresa AND p.estoque <= p.estoqueMinimo AND p.ativo = true")
    long countEstoqueBaixo(Empresa empresa);
    
    @Query("SELECT COALESCE(SUM(p.estoque * p.precoVenda), 0) FROM Produto p WHERE p.empresa = :empresa AND p.ativo = true")
    BigDecimal sumValorEstoque(Empresa empresa);
}
