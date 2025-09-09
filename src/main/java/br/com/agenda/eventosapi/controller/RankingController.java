package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.RankingOrganizadorDTO;
import br.com.agenda.eventosapi.dto.RankingParticipanteDTO;
import br.com.agenda.eventosapi.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rankings")
@Tag(name = "Rankings", description = "Endpoints para visualização de rankings da plataforma")
public class RankingController {

    @Autowired
    private RankingService rankingService;

    @Operation(summary = "Retorna o ranking de organizadores",
            description = "Lista os organizadores com base no número de eventos criados, em ordem decrescente. Endpoint público.")
    @GetMapping("/organizadores")
    public ResponseEntity<List<RankingOrganizadorDTO>> getRankingOrganizadores() {
        return ResponseEntity.ok(rankingService.getRankingOrganizadores());
    }

    @Operation(summary = "Retorna o ranking de participantes",
            description = "Lista os participantes com base no número de inscrições em eventos, em ordem decrescente. Endpoint público.")
    @GetMapping("/participantes")
    public ResponseEntity<List<RankingParticipanteDTO>> getRankingParticipantes() {
        return ResponseEntity.ok(rankingService.getRankingParticipantes());
    }
}