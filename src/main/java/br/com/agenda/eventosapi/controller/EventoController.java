package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.evento.EventoCreateDTO;
import br.com.agenda.eventosapi.dto.evento.EventoResponseDTO;
import br.com.agenda.eventosapi.service.utils.CalendarService;
import br.com.agenda.eventosapi.service.evento.EventoService;
import br.com.agenda.eventosapi.service.admin.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/eventos")
@Tag(name = "Eventos", description = "Endpoints para gestão de eventos")
public class EventoController {

    @Autowired
    private EventoService eventoService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private RelatorioService relatorioService;

    @Operation(summary = "Lista eventos com filtros (Nome, Cidade, Categoria, Data)",
            description = "Retorna uma lista paginada. Permite busca parcial por nome e cidade, e filtro exato por categoria e data.")
    @GetMapping
    @PageableAsQueryParam
    public ResponseEntity<Page<EventoResponseDTO>> listarEventos(

            @Parameter(description = "Busca por parte do nome do evento (ex: 'Festival')")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Busca por cidade (ex: 'Lisboa')")
            @RequestParam(required = false) String cidade,

            @Parameter(description = "Filtrar por categoria exata")
            @RequestParam(required = false) String categoria,

            @Parameter(description = "Filtrar por data específica (ISO: YYYY-MM-DD)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,

            @PageableDefault(size = 10, sort = "data") Pageable pageable) {

        Page<EventoResponseDTO> eventos = eventoService.listarComFiltros(nome, cidade, categoria, data, pageable);
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

    @Operation(summary = "Faz o upload de uma imagem para um evento", description = "Requer cargo de ORGANIZADOR ou ADMIN.")
    @PostMapping(value = "/{id}/imagem",  consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImagem(
            @Parameter(description = "ID do evento que receberá a imagem") @PathVariable Long id,
            @Parameter(description = "Ficheiro da imagem a ser enviado") @RequestParam("imagem") MultipartFile imagem) {

        eventoService.salvarImagem(id, imagem);
        return ResponseEntity.ok().build();
    }
    @Operation(summary = "Obtém a imagem de um evento", description = "Retorna a imagem de um evento específico.")
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> getImagem(@PathVariable Long id) {
        byte[] imagem = eventoService.getImagem(id);
        if (imagem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagem);
    }

    @Operation(summary = "Exporta a lista de inscritos de um evento para CSV",
            description = "Gera e descarrega um ficheairo CSV com os detalhes de todos os participantes inscritos. Requer o cargo de ORGANIZADOR ou ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/{id}/inscritos/exportar-csv")
    public void exportarInscritosCsv(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=inscritos_evento_" + id + ".csv";
        response.setHeader(headerKey, headerValue);

        relatorioService.gerarCsvInscritos(response.getWriter(), id);
    }

    @Operation(summary = "Exporta os detalhes de um evento para o formato iCalendar (.ics)",
            description = "Gera e descarrega um ficheiro .ics que pode ser importado para o Google Calendar, Outlook, etc. Endpoint público.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ficheiro .ics gerado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Evento não encontrado")
    })
    @GetMapping("/{id}/exportar-ical")
    public void exportarEventoIcs(
            @Parameter(description = "ID do evento a ser exportado") @PathVariable Long id,
            HttpServletResponse response) throws IOException {

        response.setContentType("text/calendar; charset=UTF-8");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=evento_" + id + ".ics";
        response.setHeader(headerKey, headerValue);

        calendarService.gerarArquivoIcsParaEvento(id, response.getWriter());
    }

    @Operation(summary = "Lista todas as cidades com eventos",
            description = "Retorna uma lista única de cidades onde existem eventos, baseada nos endereços cadastrados.")
    @GetMapping("/cidades")
    public ResponseEntity<List<String>> listarCidades() {
        List<String> cidades = eventoService.listarCidadesComEventos();
        return ResponseEntity.ok(cidades);
    }
}