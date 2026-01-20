package br.com.lavajato.repository.financeiro;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.financeiro.LancamentoFinanceiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LancamentoFinanceiroRepository extends JpaRepository<LancamentoFinanceiro, Long> {
    List<LancamentoFinanceiro> findByEmpresaAndDataBetween(Empresa empresa, LocalDateTime inicio, LocalDateTime fim);
}
