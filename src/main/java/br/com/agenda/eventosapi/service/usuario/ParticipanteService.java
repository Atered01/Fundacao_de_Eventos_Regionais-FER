package br.com.agenda.eventosapi.service.usuario;

import br.com.agenda.eventosapi.dto.evento.InscricaoRequestDTO;
import br.com.agenda.eventosapi.dto.usuario.ParticipanteDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Participante;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.ParticipanteRepository;
import br.com.agenda.eventosapi.service.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public ParticipanteDTO inscreverParticipante(InscricaoRequestDTO dto, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + eventoId));

        if (evento.getLimiteParticipantes() != null) {
            if (evento.getParticipantes().size() >= evento.getLimiteParticipantes()) {
                throw new RuntimeException("Evento lotado!");
            }
        }

        Participante novoParticipante = new Participante();
        novoParticipante.setNome(dto.nome());
        novoParticipante.setEmail(dto.email());
        novoParticipante.setEvento(evento);

        Participante participanteSalvo = participanteRepository.save(novoParticipante);

        emailService.enviarEmailConfirmacaoInscricao(participanteSalvo);

        return new ParticipanteDTO(participanteSalvo.getId(), participanteSalvo.getNome(), participanteSalvo.getEmail());
    }

    @Transactional
    public void cancelarInscricao(Long eventoId, Long participanteId) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResourceNotFoundException("Evento não encontrado com o id: " + eventoId);
        }

        Participante participante = participanteRepository.findByIdAndEventoId(participanteId, eventoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Participante com id " + participanteId + " não encontrado para o evento " + eventoId
                ));

        participanteRepository.delete(participante);
    }
}
