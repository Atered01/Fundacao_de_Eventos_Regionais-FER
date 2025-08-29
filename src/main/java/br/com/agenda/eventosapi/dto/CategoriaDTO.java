package br.com.agenda.eventosapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaDTO(Long id,
                           @NotBlank(message = "O nome da categoria não pode ser vazio.")
                           @Size(min = 3, message = "O nome da categoria deve ter no mínimo 3 caracteres.")
                           String nome,
                           String descricao) {
}
