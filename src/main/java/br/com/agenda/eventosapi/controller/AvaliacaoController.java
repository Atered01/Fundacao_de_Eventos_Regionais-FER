package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.AvaliacaoCreateDTO;
import br.com.agenda.eventosapi.dto.AvaliacaoResponseDTO;
import br.com.agenda.eventosapi.service.AvaliacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/eventos/{eventoId}/avaliacoes")
@Tag(name = "Avaliações", description = "Endpoints para gerir avaliações de eventos")
@SecurityRequirement(name = "bearer-key")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @Operation(summary = "Cria uma nova avaliação para um evento",
            description = "Permite que um utilizador autenticado que participou num evento já ocorrido submeta uma avaliação. Requer o cargo de PARTICIPANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Avaliação criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Regra de negócio violada (ex: evento não ocorreu, utilizador não participou, já avaliou)"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> criar(
            @Parameter(description = "ID do evento a ser avaliado") @PathVariable Long eventoId,
            @RequestBody @Valid AvaliacaoCreateDTO dto) {
        AvaliacaoResponseDTO novaAvaliacao = avaliacaoService.criar(eventoId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaAvaliacao);
    }

    @Operation(summary = "Lista todas as avaliações de um evento",
            description = "Retorna uma lista com todas as avaliações de um evento específico. Endpoint público.")
    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorEvento(
            @Parameter(description = "ID do evento para o qual as avaliações serão listadas") @PathVariable Long eventoId) {
        List<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.listarPorEvento(eventoId);
        return ResponseEntity.ok(avaliacoes);
    }
}