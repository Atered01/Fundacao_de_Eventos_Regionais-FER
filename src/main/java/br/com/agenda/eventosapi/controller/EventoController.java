package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.EventoCreateDTO;
import br.com.agenda.eventosapi.dto.EventoResponseDTO;
import br.com.agenda.eventosapi.service.EventoService;
import br.com.agenda.eventosapi.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@Tag(name = "Eventos", description = "Endpoints para gestão de eventos")
public class EventoController {

    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private EventoService eventoService;

    @Operation(summary = "Lista todos os eventos futuros de forma paginada",
            description = "Retorna uma lista paginada de eventos. Este endpoint é público e não requer autenticação.")
    @GetMapping
    public ResponseEntity<Page<EventoResponseDTO>> listarEventos(
            @Parameter(description = "Configuração da paginação e ordenação (ex: ?page=0&size=10&sort=data,asc)")
            @PageableDefault(size = 10, sort = "data") Pageable pageable) {
        Page<EventoResponseDTO> eventos = eventoService.listarEventosPaginado(pageable);
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Busca um evento por ID",
            description = "Retorna os detalhes de um evento específico. Este endpoint é público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento encontrado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado para o ID informado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(
            @Parameter(description = "ID do evento a ser buscado") @PathVariable Long id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cria um novo evento",
            description = "Cria um novo evento no sistema. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Evento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@RequestBody @Valid EventoCreateDTO eventoDTO) {
        EventoResponseDTO novoEvento = eventoService.salvar(eventoDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoEvento.id()).toUri();
        return ResponseEntity.created(location).body(novoEvento);
    }

    @Operation(summary = "Atualiza um evento existente",
            description = "Atualiza todos os dados de um evento. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(
            @Parameter(description = "ID do evento a ser atualizado") @PathVariable Long id,
            @RequestBody @Valid EventoCreateDTO dto) {
        EventoResponseDTO eventoAtualizado = eventoService.atualizar(id, dto);
        return ResponseEntity.ok(eventoAtualizado);
    }

    @Operation(summary = "Apaga um evento",
            description = "Remove um evento da base de dados. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evento apagado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do evento a ser apagado") @PathVariable Long id) {
        eventoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista eventos próximos a uma dada localização",
            description = "Retorna uma lista de eventos futuros dentro de um raio de distância (em km). Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Eventos encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de localização (lat/lon) em falta")
    })
    @GetMapping("/proximos")
    public ResponseEntity<List<EventoResponseDTO>> listarEventosProximos(
            @Parameter(description = "Latitude do utilizador") @RequestParam @NotNull BigDecimal lat,
            @Parameter(description = "Longitude do utilizador") @RequestParam @NotNull BigDecimal lon,
            @Parameter(description = "Raio de busca em quilómetros") @RequestParam(defaultValue = "10.0") Double radius) {

        List<EventoResponseDTO> eventos = eventoService.listarEventosProximos(lat, lon, radius);
        return ResponseEntity.ok(eventos);
    }

    @Operation(summary = "Faz o upload de uma imagem para um evento",
            description = "Associa uma imagem a um evento existente. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem enviada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PostMapping("/{id}/imagem")
    public ResponseEntity<EventoResponseDTO> uploadImagem(
            @Parameter(description = "ID do evento que receberá a imagem") @PathVariable Long id,
            @Parameter(description = "Ficheiro da imagem a ser enviado") @RequestParam("imagem") MultipartFile imagem) {
        String nomeFicheiro = fileStorageService.storeFile(imagem);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/imagens-eventos/")
                .path(nomeFicheiro)
                .toUriString();
        EventoResponseDTO eventoAtualizado = eventoService.atualizarImagem(id, fileDownloadUri);
        return ResponseEntity.ok(eventoAtualizado);
    }
}