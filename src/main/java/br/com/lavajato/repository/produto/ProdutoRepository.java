package br.com.lavajato.repository.produto;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.produto.CategoriaProduto;
import br.com.lavajato.model.produto.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findAllByEmpresaAndAtivoTrue(Empresa empresa);

    Page<Produto> findAllByEmpresaAndAtivoTrue(Empresa empresa, Pageable pageable);

    java.util.Optional<Produto> findByEmpresaAndEan(Empresa empresa, String ean);

    @Query("SELECT p FROM Produto p WHERE p.empresa = :empresa AND p.ativo = true AND (LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    Page<Produto> buscarPorTermo(Empresa empresa, String termo, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.empresa = :empresa AND p.ativo = true AND p.categoria = :categoria AND (LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')))")
    Page<Produto> buscarPorTermoECategoria(Empresa empresa, String termo, CategoriaProduto categoria,
            Pageable pageable);

    Page<Produto> findAllByEmpresaAndAtivoTrueAndCategoria(Empresa empresa, CategoriaProduto categoria,
            Pageable pageable);

    long countByEmpresaAndAtivoTrue(Empresa empresa);

    @Query("SELECT COUNT(p) FROM Produto p WHERE p.empresa = :empresa AND p.estoque <= p.estoqueMinimo AND p.ativo = true")
    long countEstoqueBaixo(Empresa empresa);

    @Query("SELECT COALESCE(SUM(p.estoque * p.precoVenda), 0) FROM Produto p WHERE p.empresa = :empresa AND p.ativo = true")
    BigDecimal sumValorEstoque(Empresa empresa);
}
