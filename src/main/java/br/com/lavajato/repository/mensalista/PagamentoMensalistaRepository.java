package br.com.lavajato.repository.mensalista;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.mensalista.PagamentoMensalista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PagamentoMensalistaRepository extends JpaRepository<PagamentoMensalista, Long> {
    List<PagamentoMensalista> findByEmpresa(Empresa empresa);
    List<PagamentoMensalista> findByMensalistaIdOrderByDataPagamentoDesc(Long mensalistaId);

    @Query("SELECT SUM(p.valorPago) FROM PagamentoMensalista p WHERE p.empresa = :empresa AND p.dataPagamento BETWEEN :inicio AND :fim")
    BigDecimal sumValorPagoByEmpresaAndPeriodo(@Param("empresa") Empresa empresa, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
