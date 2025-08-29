package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    Optional<Participante> findByIdAndEventoId(Long participanteId, Long eventoId);
}
