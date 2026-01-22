package br.com.lavajato.repository.servico;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServicoAvulsoRepository extends JpaRepository<ServicoAvulso, Long> {

    List<ServicoAvulso> findAllByEmpresaOrderByDataCriacaoDesc(Empresa empresa);

    long countByEmpresaAndStatus(Empresa empresa, StatusServico status);

    @Query("SELECT COUNT(s) FROM ServicoAvulso s WHERE s.empresa = :empresa AND s.status = 'CONCLUIDO' AND s.dataConclusao BETWEEN :inicio AND :fim")
    long countConcluidosHoje(@Param("empresa") Empresa empresa, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT COALESCE(SUM(s.valor), 0) FROM ServicoAvulso s WHERE s.empresa = :empresa AND s.status = 'CONCLUIDO' AND s.dataConclusao BETWEEN :inicio AND :fim")
    BigDecimal sumFaturamentoHoje(@Param("empresa") Empresa empresa, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("SELECT s FROM ServicoAvulso s WHERE s.empresa = :empresa AND s.dataCriacao BETWEEN :inicio AND :fim ORDER BY s.dataCriacao DESC")
    List<ServicoAvulso> findServicosDoDia(@Param("empresa") Empresa empresa, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    List<ServicoAvulso> findByEmpresaAndStatusAndDataConclusaoBetween(Empresa empresa, StatusServico status, LocalDateTime inicio, LocalDateTime fim);

    List<ServicoAvulso> findByStatusAndNotificacaoFilaEnviadaFalse(StatusServico status);

    List<ServicoAvulso> findByStatusAndNotificacaoAtrasoEnviadaFalse(StatusServico status);
}
