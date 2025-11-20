package br.com.agenda.eventosapi.dto.endereco;

import jakarta.validation.constraints.NotBlank;

public record EnderecoDTO(
        @NotBlank String logradouro,
        String numero,
        String bairro,
        @NotBlank String cidade,
        @NotBlank String estado,
        String cep
) {
}