package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.auth.*;
import br.com.agenda.eventosapi.dto.usuario.UsuarioResponseDTO;
import br.com.agenda.eventosapi.model.Usuario;
import br.com.agenda.eventosapi.service.security.TokenService;
import br.com.agenda.eventosapi.service.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints para registro e login de usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Registra um novo utilizador",
            description = "Cria uma nova conta de utilizador com o cargo padrão de PARTICIPANTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilizador registado com sucesso",
                    content = @Content(schema = @Schema(implementation = UsuarioResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou e-mail já em uso")
    })
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioResponseDTO> registrar(@RequestBody @Valid RegistroDTO dto) {
        UsuarioResponseDTO usuarioCriado = usuarioService.registrar(dto);
        return ResponseEntity.status(201).body(usuarioCriado); // Retorna 201 Created
    }


    @Operation(summary = "Autentica um utilizador",
            description = "Valida as credenciais do utilizador e retorna um token JWT para acesso aos endpoints protegidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token JWT retornado",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "Credenciais inválidas (Forbidden)")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO dto) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @Operation(summary = "Solicita a redefinição de senha",
            description = "Inicia o fluxo de redefinição de senha para um utilizador. Um e-mail com um link será enviado.")
    @PostMapping("/esqueci-senha")
    public ResponseEntity<Void> solicitarRedefinicao(@RequestBody @Valid EsqueciSenhaDTO dto) {
        usuarioService.solicitarRedefinicaoSenha(dto.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Redefine a senha do utilizador",
            description = "Finaliza o fluxo de redefinição de senha usando o token recebido por e-mail.")
    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(@RequestBody @Valid RedefinirSenhaDTO dto) {
        usuarioService.redefinirSenha(dto);
        return ResponseEntity.ok().build();
    }
}