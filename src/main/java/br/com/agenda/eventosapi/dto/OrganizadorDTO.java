package br.com.agenda.eventosapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OrganizadorDTO(Long id,
                             @NotBlank(message = "O nome do organizador não pode ser vazio.")
                              String nome,
                             @NotBlank(message = "O email do organizador não pode ser vazio.")
                              @Email(message = "O formato do email é inválido.")
                              String email) {
}
