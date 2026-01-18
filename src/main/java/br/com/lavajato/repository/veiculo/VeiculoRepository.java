package br.com.lavajato.repository.veiculo;

import br.com.lavajato.model.veiculo.Veiculo;
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
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    List<Veiculo> findAllByClienteEmpresa(Empresa empresa);
    Page<Veiculo> findAllByClienteEmpresa(Empresa empresa, Pageable pageable);
    Optional<Veiculo> findByIdAndClienteEmpresa(Long id, Empresa empresa);
    long countByClienteEmpresa(Empresa empresa);

    @Query("SELECT v FROM Veiculo v WHERE v.cliente.empresa = :empresa AND (LOWER(v.placa) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(v.cliente.nome) LIKE LOWER(CONCAT('%', :busca, '%')))")
    Page<Veiculo> buscarPorEmpresa(@Param("empresa") Empresa empresa, @Param("busca") String busca, Pageable pageable);

    @Query("SELECT v FROM Veiculo v WHERE LOWER(v.placa) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(v.modelo) LIKE LOWER(CONCAT('%', :busca, '%')) OR LOWER(v.cliente.nome) LIKE LOWER(CONCAT('%', :busca, '%'))")
    Page<Veiculo> buscarTodos(@Param("busca") String busca, Pageable pageable);
}
