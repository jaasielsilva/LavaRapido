package br.com.lavajato.service.notificacao;

import br.com.lavajato.model.notificacao.Notificacao;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.notificacao.NotificacaoRepository;
import br.com.lavajato.service.usuario.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository repository;

    @Autowired
    private UsuarioService usuarioService;

    public void criarNotificacao(Usuario usuario, String titulo, String mensagem, String link, java.time.LocalDateTime dataLimite) {
        Notificacao n = new Notificacao();
        n.setUsuario(usuario);
        n.setTitulo(titulo);
        n.setMensagem(mensagem);
        n.setLink(link);
        n.setDataLimite(dataLimite);
        repository.save(n);
    }

    public List<Notificacao> buscarNaoLidas() {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario == null) {
            return List.of();
        }
        return repository.findAtivasPorUsuario(usuario, java.time.LocalDateTime.now());
    }

    public void marcarComoLida(Long id) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        repository.findById(id).ifPresent(n -> {
            if (n.getUsuario().getId().equals(usuario.getId())) {
                n.setLida(true);
                repository.save(n);
            }
        });
    }
}
