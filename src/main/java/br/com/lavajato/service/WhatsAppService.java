package br.com.lavajato.service;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import br.com.lavajato.util.OrdemServicoMensagemUtil;

@Service
public class WhatsAppService {

    /**
     * Gera um link do WhatsApp (wa.me) com a mensagem codificada.
     * 
     * @param telefone O número do telefone (com DDI e DDD, ex: 5511999999999)
     * @param mensagem A mensagem a ser enviada
     * @return O link formatado: https://wa.me/{telefone}?text={mensagem_codificada}
     */
    public String gerarLinkWhatsApp(String telefone, String mensagem) {
        if (telefone == null || telefone.isEmpty()) {
            throw new IllegalArgumentException("O telefone não pode estar vazio.");
        }

        // Remove caracteres não numéricos do telefone
        String telefoneLimpo = telefone.replaceAll("\\D", "");

        // Se o telefone tem 10 ou 11 dígitos, assume-se que é um número brasileiro sem
        // DDI (55)
        if (telefoneLimpo.length() == 10 || telefoneLimpo.length() == 11) {
            telefoneLimpo = "55" + telefoneLimpo;
        }

        // Codifica a mensagem para URL (UTF-8)
        String mensagemCodificada = URLEncoder.encode(mensagem, StandardCharsets.UTF_8);

        return "https://wa.me/" + telefoneLimpo + "?text=" + mensagemCodificada;
    }

    /**
     * Formata a mensagem de Ordem de Serviço seguindo o modelo solicitado.
     */
        public String formatarMensagemOrdemServico(
            String numero,
            LocalDateTime data,
            String cliente,
            String veiculo,
            String placa,
            String cor,
            String servico,
            BigDecimal valor,
            String status) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataFormatada = data != null ? data.format(formatter) : "---";
        String valorFormatado = String.format("%.2f", valor != null ? valor : BigDecimal.ZERO).replace(".", ",");
        return OrdemServicoMensagemUtil.gerarMensagemOrdemServico(
            numero,
            dataFormatada,
            cliente,
            veiculo,
            placa,
            servico,
            valorFormatado,
            status
        );
        }
}
