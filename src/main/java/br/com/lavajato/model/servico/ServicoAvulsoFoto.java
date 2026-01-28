package br.com.lavajato.model.servico;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "servico_avulso_fotos")
public class ServicoAvulsoFoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_avulso_id")
    private ServicoAvulso servicoAvulso;

    @Column(nullable = false, length = 500)
    private String url; // URL pública (ex: /uploads/servicos/{empresaId}/{servicoId}/arquivo.jpg)

    @Column(nullable = false)
    private LocalDateTime dataUpload = LocalDateTime.now();
}
