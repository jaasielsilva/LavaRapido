package br.com.lavajato.repository.mensalista;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.mensalista.Mensalista;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MensalistaRepository extends JpaRepository<Mensalista, Long> {
    List<Mensalista> findByEmpresa(Empresa empresa);
    List<Mensalista> findByEmpresaAndAtivoTrue(Empresa empresa);
    
    Page<Mensalista> findByEmpresaAndAtivoTrue(Empresa empresa, Pageable pageable);
    
    long countByEmpresaAndAtivoTrue(Empresa empresa);

    @Query("SELECT SUM(m.valorMensal) FROM Mensalista m WHERE m.empresa = :empresa AND m.ativo = true")
    BigDecimal sumValorMensalByEmpresa(@Param("empresa") Empresa empresa);
    
    @Query("SELECT m FROM Mensalista m WHERE m.empresa = :empresa AND m.ativo = true AND (LOWER(m.cliente.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR m.cliente.telefone LIKE %:busca%)")
    Page<Mensalista> buscarPorTermo(@Param("empresa") Empresa empresa, @Param("busca") String busca, Pageable pageable);
}
