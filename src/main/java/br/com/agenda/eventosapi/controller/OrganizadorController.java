package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.OrganizadorDTO;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.service.OrganizadorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/organizadores")
@Tag(name = "Organizadores", description = "Endpoints para gestão de organizadores de eventos")
public class OrganizadorController {

    @Autowired
    private OrganizadorService organizadorService;

    @Operation(summary = "Lista todos os organizadores",
            description = "Retorna uma lista com todos os organizadores cadastrados. Qualquer utilizador autenticado pode aceder.")
    @GetMapping
    public ResponseEntity<List<OrganizadorDTO>> listarTodos() {
        return ResponseEntity.ok(organizadorService.listarTodos());
    }

    @Operation(summary = "Busca um organizador por ID",
            description = "Retorna os detalhes de um organizador específico. Qualquer utilizador autenticado pode aceder.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organizador encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Organizador não encontrado para o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrganizadorDTO> buscarPorId(
            @Parameter(description = "ID do organizador a ser buscado") @PathVariable Long id) {
        return organizadorService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cria um novo organizador",
            description = "Cria um novo organizador no sistema. Requer o cargo de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Organizador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    public ResponseEntity<OrganizadorDTO> criar(@RequestBody @Valid OrganizadorDTO dto) {
        OrganizadorDTO novoOrganizador = organizadorService.salvar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoOrganizador.id()).toUri();
        return ResponseEntity.created(location).body(novoOrganizador);
    }

    @Operation(summary = "Atualiza um organizador existente",
            description = "Atualiza os dados de um organizador. Requer o cargo de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Organizador atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Organizador não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrganizadorDTO> atualizar(
            @Parameter(description = "ID do organizador a ser atualizado") @PathVariable Long id,
            @RequestBody @Valid OrganizadorDTO dto) {
        OrganizadorDTO organizadorAtualizado = organizadorService.atualizar(id, dto);
        return ResponseEntity.ok(organizadorAtualizado);
    }

    @Operation(summary = "Apaga um organizador",
            description = "Remove um organizador da base de dados. Requer o cargo de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Organizador apagado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Organizador não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do organizador a ser apagado") @PathVariable Long id) {
        organizadorService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
