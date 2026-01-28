package br.com.lavajato.repository.servico;

import br.com.lavajato.model.servico.ServicoAvulsoFoto;
import br.com.lavajato.model.servico.ServicoAvulso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicoAvulsoFotoRepository extends JpaRepository<ServicoAvulsoFoto, Long> {
    List<ServicoAvulsoFoto> findByServicoAvulsoOrderByDataUploadDesc(ServicoAvulso servicoAvulso);
}
