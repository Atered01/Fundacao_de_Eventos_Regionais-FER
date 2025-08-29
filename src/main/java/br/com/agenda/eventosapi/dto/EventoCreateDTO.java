package br.com.agenda.eventosapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventoCreateDTO(@NotBlank(message = "O nome do evento não pode ser vazio.")
                              String nome,
                              @NotBlank(message = "A descrição do evento não pode ser vazia.")
                              String descricao,
                              @NotNull(message = "A data do evento é obrigatória.")
                              @Future(message = "A data do evento deve ser uma data futura.")
                              LocalDateTime data,
                              @NotNull(message = "O endereço é obrigatório.")
                              @Valid
                              EnderecoDTO endereco,
                              Integer limiteParticipantes,
                              @NotNull(message = "O ID da categoria é obrigatório.")
                              Long categoriaId,
                              @NotNull(message = "O ID do organizador é obrigatório.")
                              Long organizadorId) {
}
