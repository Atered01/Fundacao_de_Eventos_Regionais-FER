package br.com.agenda.eventosapi.dto.avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(Long id,
                                   int nota,
                                   String comentario,
                                   LocalDateTime dataAvaliacao,
                                   String nomeUsuario ) {
}
