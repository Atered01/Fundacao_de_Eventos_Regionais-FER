package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Page<Avaliacao> findByEventoId(Long eventoId, Pageable pageable);
}
