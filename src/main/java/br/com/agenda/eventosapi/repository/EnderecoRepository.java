package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Método para encontrar um endereço exato para evitar duplicações
    Optional<Endereco> findByLogradouroAndNumeroAndCidade(String logradouro, String numero, String cidade);
}