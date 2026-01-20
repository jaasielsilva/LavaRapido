package br.com.lavajato.repository.cliente;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findAllByEmpresaAndAtivoTrue(Empresa empresa);
    Page<Cliente> findAllByEmpresaAndAtivoTrue(Empresa empresa, Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM Cliente c LEFT JOIN c.veiculos v WHERE c.empresa = :empresa AND c.ativo = true AND " +
           "(LOWER(c.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(c.telefone) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(v.placa) LIKE LOWER(CONCAT('%', :busca, '%')) OR " +
           "LOWER(v.modelo) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Cliente> buscarPorTermo(@Param("empresa") Empresa empresa, @Param("busca") String busca, Pageable pageable);

    Optional<Cliente> findByIdAndEmpresaAndAtivoTrue(Long id, Empresa empresa);
    long countByEmpresaAndAtivoTrue(Empresa empresa);
    
    // Para MASTER listar todos ativos
    Page<Cliente> findAllByAtivoTrue(Pageable pageable);
}
