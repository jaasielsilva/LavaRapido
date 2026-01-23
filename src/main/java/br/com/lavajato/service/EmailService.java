package br.com.lavajato.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Async
    public void enviarEmailRecuperacaoSenha(String destinatario, String novaSenha) {
        System.out.println(">>> [EmailService] Preparando para enviar e-mail para: " + destinatario);
        System.out.println(">>> [EmailService] Remetente configurado: " + remetente);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(destinatario);
            message.setSubject("Lava Jato SaaS - Recuperação de Senha");
            message.setText("Olá,\n\n" +
                    "Você solicitou a recuperação de senha da sua conta.\n" +
                    "Sua nova senha temporária é: " + novaSenha + "\n\n" +
                    "Recomendamos que você altere esta senha após o login.\n\n" +
                    "Atenciosamente,\n" +
                    "Equipe Lava Jato SaaS");

            System.out.println(">>> [EmailService] Conectando ao servidor SMTP...");
            mailSender.send(message);
            System.out.println(">>> [EmailService] E-mail enviado com SUCESSO!");
        } catch (Exception e) {
            System.err.println(">>> [EmailService] FALHA ao enviar e-mail: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar e-mail de recuperação de senha: " + e.getMessage());
        }
    }

    @Async
    public void enviarComprovantePagamento(String destinatario, String nomeFuncionario, java.math.BigDecimal valor, java.time.LocalDateTime data, String descricao) {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject("Comprovante de Pagamento - Lava Jato SaaS");
            
            String dataFormatada = data.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            String valorFormatado = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt", "BR")).format(valor);
            
            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: Arial, sans-serif; color: #333; }
                        .container { width: 100%%; max-width: 600px; margin: 0 auto; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }
                        .header { background-color: #0ea5e9; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; }
                        .receipt-box { background-color: #f8fafc; border: 1px dashed #cbd5e1; padding: 15px; border-radius: 4px; margin: 20px 0; }
                        .row { display: flex; justify-content: space-between; margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
                        .label { font-weight: bold; color: #64748b; }
                        .value { font-weight: 600; color: #0f172a; }
                        .footer { background-color: #f1f5f9; padding: 15px; text-align: center; font-size: 12px; color: #64748b; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h2>Comprovante de Pagamento</h2>
                        </div>
                        <div class="content">
                            <p>Olá, <strong>%s</strong>!</p>
                            <p>Um novo pagamento foi registrado em seu nome.</p>
                            
                            <div class="receipt-box">
                                <div class="row">
                                    <span class="label">Descrição:</span>
                                    <span class="value">%s</span>
                                </div>
                                <div class="row">
                                    <span class="label">Data:</span>
                                    <span class="value">%s</span>
                                </div>
                                <div class="row">
                                    <span class="label">Valor:</span>
                                    <span class="value" style="color: #16a34a; font-size: 18px;">%s</span>
                                </div>
                            </div>
                            
                            <p>Este e-mail serve como comprovante digital (Holerite Simplificado).</p>
                        </div>
                        <div class="footer">
                            &copy; 2025 Lava Jato SaaS. Sistema de Gestão Inteligente.
                        </div>
                    </div>
                </body>
                </html>
                """, nomeFuncionario, descricao, dataFormatada, valorFormatado);
                
            helper.setText(htmlContent, true); // true = HTML

            System.out.println(">>> [EmailService] Enviando comprovante para: " + destinatario);
            mailSender.send(message);
            System.out.println(">>> [EmailService] Comprovante enviado com SUCESSO!");
        } catch (Exception e) {
            System.err.println(">>> [EmailService] FALHA ao enviar comprovante: " + e.getMessage());
            e.printStackTrace();
            // Não lança exceção para não travar o fluxo financeiro se o email falhar
        }
    }
}
