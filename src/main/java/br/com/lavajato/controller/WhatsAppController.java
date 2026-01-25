package br.com.lavajato.controller;

import br.com.lavajato.repository.agendamento.AgendamentoRepository;
import br.com.lavajato.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsAppController {

    @Autowired
    private WhatsAppService whatsappService;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    /**
     * Endpoint de exemplo para gerar o link de uma Ordem de Serviço (Agendamento).
     */
    @GetMapping("/link-os/{id}")
    public ResponseEntity<String> obterLinkWhatsAppOS(@PathVariable Long id) {
        return agendamentoRepository.findById(id)
                .map(agendamento -> {
                    String mensagem = whatsappService.formatarMensagemOrdemServico(
                            agendamento.getId().toString(),
                            agendamento.getData(),
                            agendamento.getVeiculo().getCliente().getNome(),
                            agendamento.getVeiculo().getModelo(),
                            agendamento.getVeiculo().getPlaca(),
                            agendamento.getVeiculo().getCor(),
                            agendamento.getServicos(),
                            agendamento.getValor(),
                            agendamento.getStatus().name());

                    String telefone = agendamento.getVeiculo().getCliente().getTelefone();
                    String link = whatsappService.gerarLinkWhatsApp(telefone, mensagem);

                    return ResponseEntity.ok(link);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para teste rápido com dados manuais.
     */
    @PostMapping("/link-manual")
    public String gerarLinkManual(
            @RequestParam String telefone,
            @RequestParam String numeroOS,
            @RequestParam String cliente,
            @RequestParam String veiculo,
            @RequestParam Double valor) {

        String mensagem = whatsappService.formatarMensagemOrdemServico(
                numeroOS,
                java.time.LocalDateTime.now(),
                cliente,
                veiculo,
                "ABC-1234",
                "Branco",
                "Lavagem Completa",
                new java.math.BigDecimal(valor),
                "CONCLUÍDO");

        return whatsappService.gerarLinkWhatsApp(telefone, mensagem);
    }
}
