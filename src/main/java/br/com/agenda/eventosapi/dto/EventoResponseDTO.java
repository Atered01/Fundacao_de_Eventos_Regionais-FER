package br.com.agenda.eventosapi.dto;

import java.time.LocalDateTime;
import java.util.List;

public record EventoResponseDTO(Long id,
                                String nome,
                                String descricao,
                                LocalDateTime data,
                                String imagemUrl,
                                EnderecoDTO endereco,
                                CategoriaDTO categoria, // Retorna o DTO da categoria, não a entidade
                                OrganizadorDTO organizador, // Retorna o DTO do organizador
                                List<ParticipanteDTO> participantes) {
}
