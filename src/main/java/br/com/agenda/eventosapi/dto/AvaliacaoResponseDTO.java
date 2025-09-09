package br.com.agenda.eventosapi.dto;

import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(Long id,
                                   int nota,
                                   String comentario,
                                   LocalDateTime dataAvaliacao,
                                   String nomeUsuario ) {
}
