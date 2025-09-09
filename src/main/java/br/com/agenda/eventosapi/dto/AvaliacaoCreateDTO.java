package br.com.agenda.eventosapi.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoCreateDTO(@NotNull(message = "A nota é obrigatória.")
                                   @Min(value = 1, message = "A nota deve ser no mínimo 1.")
                                   @Max(value = 5, message = "A nota deve ser no máximo 5.")
                                   int nota,
                                 @Size(max = 500, message = "O comentário não pode exceder os 500 caracteres.")
                                   String comentario) {
}
