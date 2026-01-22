package br.com.lavajato.job;

import br.com.lavajato.model.agendamento.Agendamento;
import br.com.lavajato.model.agendamento.StatusAgendamento;
import br.com.lavajato.model.empresa.Empresa;
import br.com.lavajato.model.usuario.Perfil;
import br.com.lavajato.model.usuario.Usuario;
import br.com.lavajato.repository.agendamento.AgendamentoRepository;
import br.com.lavajato.repository.usuario.UsuarioRepository;
import br.com.lavajato.service.notificacao.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AgendamentoAlertaJob {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Scheduled(cron = "0 * * * * *") // Executa a cada minuto
    @Transactional
    public void verificarAgendamentosProximos() {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        // Busca agendamentos que começam daqui a 5 minutos (intervalo de 1 minuto)
        LocalDateTime inicio = now.plusMinutes(5);
        LocalDateTime fim = inicio.plusSeconds(59);

        List<Agendamento> agendamentos = agendamentoRepository.findByDataBetweenAndStatus(inicio, fim, StatusAgendamento.AGENDADO);

        for (Agendamento a : agendamentos) {
            Empresa empresa = a.getVeiculo().getCliente().getEmpresa();
            
            // Notificar ADMINs da empresa
            List<Usuario> admins = usuarioRepository.findByEmpresaAndPerfilAndAtivoTrue(empresa, Perfil.ADMIN);
            
            // Também notificar MASTER se houver (opcional, mas o user pediu "pra mim ADMIN", vou incluir MASTER pois geralmente é o dono)
            List<Usuario> masters = usuarioRepository.findByEmpresaAndPerfilAndAtivoTrue(empresa, Perfil.MASTER);
            admins.addAll(masters);

            String horaFormatada = a.getData().format(DateTimeFormatter.ofPattern("HH:mm"));
            String mensagem = String.format("Cliente: %s\nVeículo: %s\nHorário: %s", 
                a.getVeiculo().getCliente().getNome(),
                a.getVeiculo().getModelo() + " - " + a.getVeiculo().getPlaca(),
                horaFormatada
            );

            for (Usuario admin : admins) {
                // Evitar duplicidade se o usuário for retornado em ambas as listas (improvável pelo perfil, mas seguro)
                // Na verdade, perfil é único, então sem duplicidade.
                
                notificacaoService.criarNotificacao(
                    admin,
                    "⏰ Agendamento em 5 min",
                    mensagem,
                    "/agendamentos",
                    a.getData() // Expira na hora do agendamento
                );
            }
        }
    }
}
