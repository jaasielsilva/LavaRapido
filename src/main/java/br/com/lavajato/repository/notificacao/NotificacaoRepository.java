package br.com.lavajato.repository.notificacao;

import br.com.lavajato.model.notificacao.Notificacao;
import br.com.lavajato.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    
    @Query("SELECT n FROM Notificacao n WHERE n.usuario = :usuario AND n.lida = false AND (n.dataLimite IS NULL OR n.dataLimite > :agora) ORDER BY n.dataCriacao DESC")
    List<Notificacao> findAtivasPorUsuario(@Param("usuario") Usuario usuario, @Param("agora") LocalDateTime agora);
}
