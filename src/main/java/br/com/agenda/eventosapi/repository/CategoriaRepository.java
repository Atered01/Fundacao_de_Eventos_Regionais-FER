package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
