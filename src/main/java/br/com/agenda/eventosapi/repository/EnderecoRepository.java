package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Endereco;
import br.com.agenda.eventosapi.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    Optional<Endereco> findByLogradouroAndNumeroAndCidade(String logradouro, String numero, String cidade);
}