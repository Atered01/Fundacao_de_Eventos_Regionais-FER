package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.CategoriaDTO;
import br.com.agenda.eventosapi.service.CategoriaService;
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
@RequestMapping("/categorias")
@Tag(name = "Categorias", description = "Endpoints para gestão de categorias de eventos")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Lista todas as categorias",
            description = "Retorna uma lista com todas as categorias cadastradas. Este endpoint é público.")
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        return ResponseEntity.ok(categoriaService.listarTodas());
    }

    @Operation(summary = "Busca uma categoria por ID",
            description = "Retorna uma categoria específica pelo seu ID. Este endpoint é público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria encontrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada para o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(
            @Parameter(description = "ID da categoria a ser buscada") @PathVariable Long id) {
        return categoriaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cria uma nova categoria",
            description = "Cria uma nova categoria de evento. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    public ResponseEntity<CategoriaDTO> criar(@RequestBody @Valid CategoriaDTO categoriaDTO) {
        CategoriaDTO novaCategoria = categoriaService.salvar(categoriaDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novaCategoria.id()).toUri();
        return ResponseEntity.created(location).body(novaCategoria);
    }

    @Operation(summary = "Atualiza uma categoria existente",
            description = "Atualiza os dados de uma categoria existente. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada para o ID informado"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CategoriaDTO> atualizar(
            @Parameter(description = "ID da categoria a ser atualizada") @PathVariable Long id,
            @RequestBody @Valid CategoriaDTO categoriaDTO) {

        CategoriaDTO categoriaAtualizada = categoriaService.atualizar(id, categoriaDTO);
        return ResponseEntity.ok(categoriaAtualizada);
    }

    @Operation(summary = "Apaga uma categoria",
            description = "Remove uma categoria existente da base de dados. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoria apagada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada para o ID informado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID da categoria a ser apagada") @PathVariable Long id) {
        categoriaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}