package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.RankingOrganizadorDTO;
import br.com.agenda.eventosapi.dto.RankingParticipanteDTO;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.ParticipanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RankingService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Transactional(readOnly = true)
    public List<RankingOrganizadorDTO> getRankingOrganizadores() {
        return eventoRepository.findRankingOrganizadores();
    }

    @Transactional(readOnly = true)
    public List<RankingParticipanteDTO> getRankingParticipantes() {
        return participanteRepository.findRankingParticipantes();
    }
}