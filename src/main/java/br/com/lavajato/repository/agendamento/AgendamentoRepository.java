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
}
