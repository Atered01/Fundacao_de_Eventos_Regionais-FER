package br.com.agenda.eventosapi.service.utils;

import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TarefaAgendadaService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EmailService emailService; // Reutilizamos o nosso serviço de e-mail

    /**
     * Este método é executado automaticamente todos os dias às 08:00 da manhã.
     * Ele procura por eventos que acontecerão nas próximas 24 horas e envia
     * um lembrete por e-mail a todos os participantes inscritos.
     */
    @Scheduled(cron = "0 0 8 * * *") // Executa todos os dias às 8h da manhã
    public void enviarLembretesDeEventos() {
        System.out.println("Executando tarefa agendada: Verificando eventos para enviar lembretes...");

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime daqui24Horas = agora.plusHours(24);

        // 1. Busca os eventos que acontecerão nas próximas 24 horas
        List<Evento> eventosProximos = eventoRepository.findAllByDataBetween(agora, daqui24Horas);

        // 2. Para cada evento encontrado, envia o lembrete
        for (Evento evento : eventosProximos) {
            System.out.println("Enviando lembretes para o evento: " + evento.getNome());
            // O método de envio de e-mail precisa ser criado no EmailService
            emailService.enviarEmailLembreteEvento(evento);
        }

        System.out.println("Tarefa de lembretes concluída.");
    }
}