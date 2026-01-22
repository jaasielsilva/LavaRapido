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
    
    public String getNomeClienteDisplay() {
        if (cliente != null) {
            return cliente.getNome();
        }
        return clienteAvulsoNome != null ? clienteAvulsoNome : "Cliente Avulso";
    }
    
    public String getVeiculoDisplay() {
        if (cliente != null && !cliente.getVeiculos().isEmpty()) {
            // Pega o primeiro veículo para simplificar na listagem se não foi selecionado especificamente
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
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDE97 *ORDEM DE SERVIÇO #").append(id).append("*").append("\n\n");
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");
        sb.append("\uD83D\uDCC5 *Data:* ").append(dataCriacao.format(dtf)).append("\n\n");
        
        sb.append("\uD83D\uDC64 *Cliente:* ").append(getNomeClienteDisplay()).append("\n");
        
        if (cliente != null && !cliente.getVeiculos().isEmpty()) {
             var v = cliente.getVeiculos().get(0);
             sb.append("\uD83D\uDE98 *Veículo:* ").append(v.getModelo()).append("\n");
             sb.append("\uD83D\uDD16 *Placa:* ").append(v.getPlaca()).append("\n");
             if (v.getCor() != null && !v.getCor().isEmpty()) {
                 sb.append("\uD83C\uDFA8 *Cor:* ").append(v.getCor()).append("\n");
             }
        } else if (clienteAvulsoVeiculo != null) {
            sb.append("\uD83D\uDE98 *Veículo:* ").append(clienteAvulsoVeiculo).append("\n");
        }
        
        sb.append("\n\uD83E\uDDFD *Serviços:*\n");
        String nomeServico = servico != null ? servico.getNome() : nomeServicoHistorico;
        sb.append("• ").append(nomeServico).append("\n\n");
        
        String valorFormatado = "R$ " + String.format("%.2f", valor != null ? valor : BigDecimal.ZERO);
        sb.append("\uD83D\uDCB0 *Total:* ").append(valorFormatado).append("\n\n");
        
        sb.append("\u2705 *Status:* ").append(status.getDescricao()).append("\n\n");
        
        sb.append("_RAlavarapido_"); // Assinatura
        
        return sb.toString();
    }
    
    public String getLinkWhatsapp() {
        String telefone = null;
        if (cliente != null && cliente.getTelefone() != null) {
            telefone = cliente.getTelefone().replaceAll("\\D", "");
        }
        
        // Se não tiver telefone, retorna link sem número (abre lista de contatos)
        String baseUrl = "https://wa.me/";
        if (telefone != null && !telefone.isEmpty()) {
            if (!telefone.startsWith("55")) {
                telefone = "55" + telefone;
            }
            baseUrl += telefone;
        }
        
        try {
            return baseUrl + "?text=" + URLEncoder.encode(getMensagemWhatsapp(), StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return baseUrl;
        }
    }
}
