package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.PerfilUpdateDTO;
import br.com.agenda.eventosapi.dto.auth.UsuarioResponseDTO;
import br.com.agenda.eventosapi.service.PerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Operation(summary = "Atualiza os dados de texto do perfil do utilizador autenticado")
    @PutMapping
    public ResponseEntity<UsuarioResponseDTO> updatePerfil(Authentication authentication, @RequestBody @Valid PerfilUpdateDTO dto) {
        return ResponseEntity.ok(perfilService.updatePerfil(authentication, dto));
    }

    @Operation(summary = "Faz o upload da foto de perfil do utilizador autenticado")
    @PostMapping("/imagem")
    public ResponseEntity<Void> uploadImagemPerfil(Authentication authentication, @RequestParam("imagem") MultipartFile imagem) {
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