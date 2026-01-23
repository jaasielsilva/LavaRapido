package br.com.lavajato.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

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
}
