package br.com.lavajato.model.usuario;

import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Perfil perfil;

    private boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    public boolean isMaster() {
        return Perfil.MASTER.equals(this.perfil);
    }
    
    @ManyToOne
    @JoinColumn(name = "excluido_por_id")
    private Usuario excluidoPor;
    
    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;
}
