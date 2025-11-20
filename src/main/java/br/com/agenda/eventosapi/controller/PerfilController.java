package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.evento.EventoResponseDTO;
import br.com.agenda.eventosapi.dto.evento.InscricaoResponseDTO;
import br.com.agenda.eventosapi.dto.usuario.PerfilUpdateDTO;
import br.com.agenda.eventosapi.dto.usuario.UsuarioResponseDTO;
import br.com.agenda.eventosapi.service.usuario.PerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/perfil")
@Tag(name = "Perfil do Utilizador", description = "Endpoints para o utilizador autenticado gerir o seu próprio perfil")
@SecurityRequirement(name = "bearer-key")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @Operation(summary = "Obtém os dados do perfil do utilizador autenticado")
    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> getPerfil(Authentication authentication) {
        return ResponseEntity.ok(perfilService.getPerfil(authentication));
    }


    @Operation(summary = "Lista os eventos criados pelo utilizador autenticado",
            description = "Retorna uma lista paginada de todos os eventos onde o utilizador autenticado é o organizador.")
    @GetMapping("/meus-eventos")
    public ResponseEntity<Page<EventoResponseDTO>> getMeusEventos(
            Authentication authentication,
            @Parameter(description = "Configuração da paginação (ex: ?page=0&size=5)")
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(perfilService.getMeusEventos(authentication, pageable));
    }

    @Operation(summary = "Lista as inscrições do utilizador autenticado",
            description = "Retorna uma lista paginada de todos os eventos nos quais o utilizador autenticado está inscrito.")
    @GetMapping("/minhas-inscricoes")
    public ResponseEntity<Page<InscricaoResponseDTO>> getMinhasInscricoes(
            Authentication authentication,
            @Parameter(description = "Configuração da paginação (ex: ?page=0&size=5)")
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(perfilService.getMinhasInscricoes(authentication, pageable));
    }


    @Operation(summary = "Atualiza os dados de texto do perfil do utilizador autenticado")
    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> updatePerfil(Authentication authentication, @RequestBody @Valid PerfilUpdateDTO dto) {
        return ResponseEntity.ok(perfilService.updatePerfil(authentication, dto));
    }

    @Operation(summary = "Faz o upload da foto de perfil do utilizador autenticado")
    @PostMapping(value = "/imagem", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImagemPerfil(
            Authentication authentication,
            @Parameter(description = "Ficheiro da imagem a ser enviado") @RequestParam("imagem") MultipartFile imagem) {
        String userEmail = authentication.getName();
        perfilService.salvarImagemPerfil(userEmail, imagem);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtém a foto de perfil de um utilizador por ID")
    @GetMapping("/{id}/imagem")
    public ResponseEntity<byte[]> getImagemPerfil(@PathVariable Long id) {
        byte[] imagem = perfilService.getImagemPerfil(id);
        if (imagem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imagem);
    }
}