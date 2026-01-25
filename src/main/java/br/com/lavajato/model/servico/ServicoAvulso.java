package br.com.lavajato.model.servico;

import br.com.lavajato.model.cliente.Cliente;
import br.com.lavajato.model.empresa.Empresa;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import br.com.lavajato.util.OrdemServicoMensagemUtil;

@Data
@Entity
@Table(name = "servicos_avulsos")
public class ServicoAvulso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusServico status = StatusServico.EM_FILA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "servico_id")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Servico servico;

    // Campos históricos para garantir que o preço não mude se o catálogo mudar
    private String nomeServicoHistorico;
    private BigDecimal valor;

    // Cliente Cadastrado (Opcional)
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Cliente cliente;

    // Cliente Avulso (Opcional)
    private String clienteAvulsoNome;
    private String clienteAvulsoVeiculo; // Modelo - Placa

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean usoFidelidade = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notificacaoFilaEnviada = false;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notificacaoAtrasoEnviada = false;

    @Column(length = 20, unique = false)
    private String protocolo;

    public String getNomeClienteDisplay() {
        if (cliente != null) {
            return cliente.getNome();
        }
        return clienteAvulsoNome != null ? clienteAvulsoNome : "Cliente Avulso";
    }

    public String getVeiculoDisplay() {
        if (cliente != null && !cliente.getVeiculos().isEmpty()) {
            // Pega o primeiro veículo para simplificar na listagem se não foi selecionado
            // especificamente
            // Idealmente teríamos o Veículo selecionado aqui também, mas vamos simplificar
            var v = cliente.getVeiculos().get(0);
            return v.getModelo() + " - " + v.getPlaca();
        }
        return clienteAvulsoVeiculo != null ? clienteAvulsoVeiculo : "N/A";
    }

    public String getDuracaoFormatada() {
        if (dataInicio == null || dataConclusao == null) {
            return "-";
        }
        java.time.Duration duration = java.time.Duration.between(dataInicio, dataConclusao);
        long horas = duration.toHours();
        long minutos = duration.toMinutesPart();

        if (horas > 0) {
            return String.format("%dh %dm", horas, minutos);
        } else {
            return String.format("%dm", minutos);
        }
    }

    public String getMensagemWhatsapp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        String dataFormatada = dataCriacao != null ? dataCriacao.format(dtf) : "---";

        String veiculo = "N/A";
        String placa = "N/A";
        if (cliente != null && !cliente.getVeiculos().isEmpty()) {
            var v = cliente.getVeiculos().get(0);
            veiculo = v.getModelo();
            placa = v.getPlaca();
        } else if (clienteAvulsoVeiculo != null) {
            veiculo = clienteAvulsoVeiculo;
        }

        String nomeServico = servico != null ? servico.getNome() : nomeServicoHistorico;
        String valorFormatado = String.format("%.2f", valor != null ? valor : BigDecimal.ZERO).replace(".", ",");

        return OrdemServicoMensagemUtil.gerarMensagemOrdemServico(
            protocolo != null && !protocolo.isBlank() ? protocolo : (id != null ? id.toString() : "-"),
            dataFormatada,
            getNomeClienteDisplay(),
            veiculo,
            placa,
            nomeServico,
            valorFormatado,
            status != null ? status.getDescricao() : "-"
        );
    }

    public String getLinkWhatsapp() {
        String telefone = null;
        if (cliente != null && cliente.getTelefone() != null) {
            telefone = cliente.getTelefone().replaceAll("\\D", "");
            // Se o telefone tem 10 ou 11 dígitos, assume-se que é um número brasileiro sem
            // DDI (55)
            if (telefone.length() == 10 || telefone.length() == 11) {
                telefone = "55" + telefone;
            }
        }

        String baseUrl = "https://wa.me/";
        if (telefone != null && !telefone.isEmpty()) {
            baseUrl += telefone;
        }

        try {
            String message = getMensagemWhatsapp();
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            return baseUrl + "?text=" + encodedMessage;
        } catch (Exception e) {
            return baseUrl;
        }
    }

    public boolean isAtrasado() {
        if (servico == null || servico.getTempoMinutos() == null || servico.getTempoMinutos() <= 0) {
            return false;
        }

        if (dataInicio == null) {
            return false;
        }

        // Only relevant for EM_ANDAMENTO or CONCLUIDO
        if (status != StatusServico.EM_ANDAMENTO && status != StatusServico.CONCLUIDO) {
            return false;
        }

        LocalDateTime fim = dataConclusao != null ? dataConclusao : LocalDateTime.now();
        long minutosDecorridos = java.time.Duration.between(dataInicio, fim).toMinutes();

        return minutosDecorridos > servico.getTempoMinutos();
    }
}
