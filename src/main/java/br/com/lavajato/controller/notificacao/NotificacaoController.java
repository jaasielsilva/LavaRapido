package br.com.lavajato.controller.notificacao;

import br.com.lavajato.model.notificacao.Notificacao;
import br.com.lavajato.service.notificacao.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService service;

    @GetMapping("/check")
    public ResponseEntity<List<Notificacao>> verificarNotificacoes() {
        List<Notificacao> notificacoes = service.buscarNaoLidas();
        return ResponseEntity.ok(notificacoes);
    }

    @PostMapping("/{id}/ler")
    public ResponseEntity<Void> marcarComoLida(@PathVariable Long id) {
        service.marcarComoLida(id);
        return ResponseEntity.ok().build();
    }
}
