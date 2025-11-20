package br.com.agenda.eventosapi.dto.evento;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record InscricaoRequestDTO(  @NotBlank(message = "O nome do participante não pode ser vazio.")
                                    String nome,
                                    @NotBlank(message = "O email do participante não pode ser vazio.")
                                    @Email(message = "O formato do email é inválido.")
                                    String email) {
}