package br.com.agenda.eventosapi.dto;

import java.time.Instant;

// DTO para padronizar as respostas de erro da API.
public record ErrorResponseDTO(
        String mensagem,
        int status,
        Instant timestamp
) {
}