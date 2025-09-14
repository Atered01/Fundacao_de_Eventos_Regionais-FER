package br.com.agenda.eventosapi.dto;

import jakarta.validation.constraints.Size;

public record PerfilUpdateDTO(
        @Size(max = 255) String nome,
        @Size(max = 500) String biografia,
        @Size(max = 100) String cidade
) {
}