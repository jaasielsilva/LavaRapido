package br.com.lavajato.model.notificacao;

import br.com.lavajato.model.usuario.Usuario;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notificacoes")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Link opcional para ação (ex: /agendamentos/1)
    private String link;

    private LocalDateTime dataLimite;
}
