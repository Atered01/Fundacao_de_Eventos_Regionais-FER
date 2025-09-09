package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.DashboardStatsDTO;
import br.com.agenda.eventosapi.dto.auth.UsuarioResponseDTO;
import br.com.agenda.eventosapi.model.UsuarioRole;
import br.com.agenda.eventosapi.service.AdminService;
import br.com.agenda.eventosapi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Endpoints para gestão de utilizadores (requer cargo ADMIN)")
@SecurityRequirement(name = "bearer-key") // Diz ao Swagger para exigir autenticação
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private AdminService adminService;

    @Operation(summary = "Lista todos os utilizadores do sistema",
            description = "Retorna uma lista com os dados públicos de todos os utilizadores registados. Requer o cargo de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de utilizadores retornada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas utilizadores com cargo ADMIN podem aceder a este recurso.")
    })
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listarUtilizadores() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @Operation(summary = "Altera o cargo (role) de um utilizador específico",
            description = "Promove ou rebaixa um utilizador para um novo cargo (ADMIN, ORGANIZADOR, PARTICIPANTE). Requer o cargo de ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cargo do utilizador alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Utilizador não encontrado para o ID informado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado. Apenas utilizadores com cargo ADMIN podem aceder a este recurso.")
    })
    @PutMapping("/usuarios/{id}/role")
    public ResponseEntity<UsuarioResponseDTO> alterarCargo(
            @Parameter(description = "ID do utilizador a ser modificado") @PathVariable Long id,
            @Parameter(description = "O novo cargo a ser atribuído (ADMIN, ORGANIZADOR, ou PARTICIPANTE)") @RequestParam("role") UsuarioRole role) {
        UsuarioResponseDTO utilizadorAtualizado = usuarioService.alterarCargo(id, role);
        return ResponseEntity.ok(utilizadorAtualizado);
    }

    @Operation(summary = "Obtém estatísticas do dashboard", description = "Retorna os KPIs principais da plataforma. Requer cargo de ADMIN.")
    @GetMapping("/estatisticas")
    public ResponseEntity<DashboardStatsDTO> getEstatisticas() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }
}