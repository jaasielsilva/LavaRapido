package br.com.lavajato.repository.agendamento;

import br.com.lavajato.model.agendamento.Agendamento;
import br.com.lavajato.model.empresa.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    @Query("SELECT a FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa ORDER BY a.data ASC")
    Page<Agendamento> findAllByEmpresa(@Param("empresa") Empresa empresa, Pageable pageable);

    @Query("SELECT a FROM Agendamento a WHERE a.id = :id AND a.veiculo.cliente.empresa = :empresa")
    Optional<Agendamento> findByIdAndEmpresa(@Param("id") Long id, @Param("empresa") Empresa empresa);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.status IN ('AGENDADO','EM_ANDAMENTO')")
    long countPendentesByEmpresa(@Param("empresa") Empresa empresa);

    @Query("SELECT a FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.status IN ('AGENDADO') AND a.data >= CURRENT_TIMESTAMP ORDER BY a.data ASC")
    Page<Agendamento> findProximosByEmpresa(@Param("empresa") Empresa empresa, Pageable pageable);

    @Query("SELECT a FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa " +
            "AND (:dataInicio IS NULL OR a.data >= :dataInicio) " +
            "AND (:dataFim IS NULL OR a.data <= :dataFim) " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:busca IS NULL OR lower(a.veiculo.cliente.nome) LIKE lower(concat('%', :busca, '%')) OR lower(a.veiculo.placa) LIKE lower(concat('%', :busca, '%'))) " +
            "ORDER BY a.data ASC")
    Page<Agendamento> findComFiltros(@Param("empresa") Empresa empresa,
                                     @Param("dataInicio") java.time.LocalDateTime dataInicio,
                                     @Param("dataFim") java.time.LocalDateTime dataFim,
                                     @Param("status") br.com.lavajato.model.agendamento.StatusAgendamento status,
                                     @Param("busca") String busca,
                                     Pageable pageable);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.data BETWEEN :inicio AND :fim")
    long countByEmpresaAndDataBetween(@Param("empresa") Empresa empresa, @Param("inicio") java.time.LocalDateTime inicio, @Param("fim") java.time.LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.status = :status AND a.data BETWEEN :inicio AND :fim")
    long countByEmpresaAndStatusAndDataBetween(@Param("empresa") Empresa empresa, @Param("status") br.com.lavajato.model.agendamento.StatusAgendamento status, @Param("inicio") java.time.LocalDateTime inicio, @Param("fim") java.time.LocalDateTime fim);

    @Query("SELECT COUNT(a) FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.status <> :status AND a.data BETWEEN :inicio AND :fim")
    long countByEmpresaAndStatusNotAndDataBetween(@Param("empresa") Empresa empresa, @Param("status") br.com.lavajato.model.agendamento.StatusAgendamento status, @Param("inicio") java.time.LocalDateTime inicio, @Param("fim") java.time.LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(a.valor), 0) FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.status = :status AND a.data BETWEEN :inicio AND :fim")
    java.math.BigDecimal sumValorByEmpresaAndStatusAndDataBetween(@Param("empresa") Empresa empresa, @Param("status") br.com.lavajato.model.agendamento.StatusAgendamento status, @Param("inicio") java.time.LocalDateTime inicio, @Param("fim") java.time.LocalDateTime fim);

    @Query("SELECT a FROM Agendamento a WHERE a.veiculo.cliente.empresa = :empresa AND a.status = :status AND a.data BETWEEN :inicio AND :fim")
    java.util.List<Agendamento> findByEmpresaAndStatusAndDataBetween(@Param("empresa") Empresa empresa, @Param("status") br.com.lavajato.model.agendamento.StatusAgendamento status, @Param("inicio") java.time.LocalDateTime inicio, @Param("fim") java.time.LocalDateTime fim);

    java.util.List<Agendamento> findByDataBetweenAndStatus(java.time.LocalDateTime start, java.time.LocalDateTime end, br.com.lavajato.model.agendamento.StatusAgendamento status);
}
