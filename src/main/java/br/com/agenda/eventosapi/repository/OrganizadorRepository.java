package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Organizador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizadorRepository extends JpaRepository<Organizador, Long> {
}
