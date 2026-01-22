package br.com.lavajato.repository.venda;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.venda.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {
    List<Venda> findAllByEmpresaOrderByDataVendaDesc(Empresa empresa);
    List<Venda> findByEmpresaAndDataVendaBetween(Empresa empresa, LocalDateTime inicio, LocalDateTime fim);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(v.valorTotal), 0) FROM Venda v WHERE v.empresa = :empresa AND v.dataVenda BETWEEN :inicio AND :fim")
    java.math.BigDecimal sumFaturamentoPorPeriodo(Empresa empresa, LocalDateTime inicio, LocalDateTime fim);
}
