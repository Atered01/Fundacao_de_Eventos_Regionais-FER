package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
}
