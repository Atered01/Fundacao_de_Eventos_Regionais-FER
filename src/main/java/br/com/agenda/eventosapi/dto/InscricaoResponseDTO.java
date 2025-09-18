package br.com.agenda.eventosapi.dto;

import br.com.agenda.eventosapi.model.Participante;

import java.time.LocalDateTime;

public record InscricaoResponseDTO(
        Long idInscricao,
        Long idEvento,
        String nomeEvento,
        LocalDateTime dataEvento,
        String nomeOrganizador
) {
    public static InscricaoResponseDTO fromEntity(Participante participante) {
        return new InscricaoResponseDTO(
                participante.getId(),
                participante.getEvento().getId(),
                participante.getEvento().getNome(),
                participante.getEvento().getData(),
                participante.getEvento().getOrganizador().getNome()
        );
    }
}