package br.com.lavajato.job;

import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.servico.ServicoAvulso;
import br.com.lavajato.model.servico.StatusServico;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.servico.ServicoAvulsoRepository;
import br.com.lavajato.repository.usuario.UsuarioRepository;
import br.com.lavajato.service.notificacao.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ServicoAlertaJob {

    @Autowired
    private ServicoAvulsoRepository servicoAvulsoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Scheduled(cron = "0 * * * * *") // Executa a cada minuto
    @Transactional
    public void verificarAlertasServicos() {
        LocalDateTime now = LocalDateTime.now();

        verificarTempoFila(now);
        verificarServicosAtrasados(now);
    }

    private void verificarTempoFila(LocalDateTime now) {
        // Veículos em FILA há mais de 15 minutos
        List<ServicoAvulso> servicosFila = servicoAvulsoRepository.findByStatusAndNotificacaoFilaEnviadaFalse(StatusServico.EM_FILA);

        for (ServicoAvulso s : servicosFila) {
            long minutosFila = ChronoUnit.MINUTES.between(s.getDataCriacao(), now);
            
            if (minutosFila > 15) {
                enviarNotificacao(s, "⚠️ Veículo muito tempo na fila", 
                    String.format("O veículo %s está na fila há %d minutos (limite: 15min).", 
                    s.getVeiculoDisplay(), minutosFila),
                    "/servicos-avulsos?highlight=" + s.getId());
                
                s.setNotificacaoFilaEnviada(true);
                servicoAvulsoRepository.save(s);
            }
        }
    }

    private void verificarServicosAtrasados(LocalDateTime now) {
        // Serviços EM ANDAMENTO que estouraram o tempo estimado
        List<ServicoAvulso> servicosAndamento = servicoAvulsoRepository.findByStatusAndNotificacaoAtrasoEnviadaFalse(StatusServico.EM_ANDAMENTO);

        for (ServicoAvulso s : servicosAndamento) {
            if (s.getDataInicio() == null) continue;

            // Determina tempo estimado (do serviço atual ou histórico)
            int tempoEstimado = 30; // Default
            if (s.getServico() != null && s.getServico().getTempoMinutos() != null) {
                tempoEstimado = s.getServico().getTempoMinutos();
            }

            LocalDateTime dataEstimadaFim = s.getDataInicio().plusMinutes(tempoEstimado);
            
            if (now.isAfter(dataEstimadaFim)) {
                long minutosAtraso = ChronoUnit.MINUTES.between(dataEstimadaFim, now);
                
                enviarNotificacao(s, "⏰ Serviço Atrasado", 
                    String.format("O serviço %s no veículo %s está atrasado há %d minutos.", 
                    s.getServico() != null ? s.getServico().getNome() : s.getNomeServicoHistorico(),
                    s.getVeiculoDisplay(), minutosAtraso),
                    "/servicos-avulsos?highlight=" + s.getId());
                
                s.setNotificacaoAtrasoEnviada(true);
                servicoAvulsoRepository.save(s);
            }
        }
    }

    private void enviarNotificacao(ServicoAvulso s, String titulo, String mensagem, String link) {
        Empresa empresa = s.getEmpresa();
        
        // Notificar ADMINs e MASTERs da empresa
        List<Usuario> admins = usuarioRepository.findByEmpresaAndPerfilAndAtivoTrue(empresa, Perfil.ADMIN);
        List<Usuario> masters = usuarioRepository.findByEmpresaAndPerfilAndAtivoTrue(empresa, Perfil.MASTER);
        admins.addAll(masters);

        // Remove duplicatas caso exista (embora perfil seja unico)
        List<Usuario> destinatarios = admins.stream().distinct().toList();

        for (Usuario u : destinatarios) {
            notificacaoService.criarNotificacao(
                u,
                titulo,
                mensagem,
                link, // Link para a lista com highlight
                LocalDateTime.now().plusHours(24) // Expira em 24h
            );
        }
    }
}
