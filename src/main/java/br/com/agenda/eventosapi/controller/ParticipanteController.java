package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.InscricaoRequestDTO;
import br.com.agenda.eventosapi.dto.ParticipanteDTO;
import br.com.agenda.eventosapi.service.ParticipanteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eventos/{eventoId}/participantes")
@Tag(name = "Inscrições", description = "Endpoints para inscrever e cancelar participação em eventos")
public class ParticipanteController {

    @Autowired
    private ParticipanteService participanteService;

    @Operation(summary = "Inscreve um utilizador num evento",
            description = "Cria uma nova inscrição para o utilizador autenticado no evento especificado. Requer o cargo de PARTICIPANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inscrição realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou evento lotado"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado para o ID informado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping("/inscrever")
    public ResponseEntity<ParticipanteDTO> inscrever(
            @Parameter(description = "ID do evento no qual se inscrever") @PathVariable Long eventoId,
            @RequestBody @Valid InscricaoRequestDTO inscricaoDTO) {
        ParticipanteDTO novoParticipante = participanteService.inscreverParticipante(inscricaoDTO, eventoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoParticipante);
    }

    @Operation(summary = "Cancela a inscrição de um participante em um evento",
            description = "Remove a inscrição de um utilizador de um evento. Requer o cargo de PARTICIPANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Inscrição cancelada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento ou inscrição não encontrada"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{participanteId}")
    public ResponseEntity<Void> cancelarInscricao(
            @Parameter(description = "ID do evento do qual cancelar a inscrição") @PathVariable Long eventoId,
            @Parameter(description = "ID do participante (inscrição) a ser cancelado") @PathVariable Long participanteId) {
        participanteService.cancelarInscricao(eventoId, participanteId);
        return ResponseEntity.noContent().build();
    }
}