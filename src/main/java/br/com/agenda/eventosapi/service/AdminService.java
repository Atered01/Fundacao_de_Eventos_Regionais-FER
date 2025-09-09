package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.DashboardStatsDTO;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.ParticipanteRepository;
import br.com.agenda.eventosapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    public DashboardStatsDTO getDashboardStats() {
        long totalUsuarios = usuarioRepository.count();
        long totalEventosAtivos = eventoRepository.countByDataAfter(LocalDateTime.now());
        long totalInscricoes = participanteRepository.count();
        long novosUsuarios = usuarioRepository.countByDataRegistoAfter(LocalDateTime.now().minusDays(7));

        return new DashboardStatsDTO(totalUsuarios, totalEventosAtivos, totalInscricoes, novosUsuarios);
    }
}
