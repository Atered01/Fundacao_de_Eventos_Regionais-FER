package br.com.agenda.eventosapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EsqueciSenhaDTO(
        @NotBlank(message = "O email é obrigatório.")
        @Email(message = "Formato de email inválido.")
        String email
) {
}