package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.dto.ranking.RankingParticipanteDTO;
import br.com.agenda.eventosapi.model.Participante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    Optional<Participante> findByIdAndEventoId(Long participanteId, Long eventoId);
    Page<Participante> findByEmail(String email, Pageable pageable);
    @Query("""
        SELECT new br.com.agenda.eventosapi.dto.ranking.RankingParticipanteDTO(p.nome, COUNT(p))
        FROM Participante p
        GROUP BY p.email, p.nome
        ORDER BY COUNT(p) DESC
    """)
    List<RankingParticipanteDTO> findRankingParticipantes();
}
