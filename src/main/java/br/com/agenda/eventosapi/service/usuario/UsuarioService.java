package br.com.agenda.eventosapi.service.usuario;

import br.com.agenda.eventosapi.dto.auth.RedefinirSenhaDTO;
import br.com.agenda.eventosapi.dto.auth.RegistroDTO;
import br.com.agenda.eventosapi.dto.usuario.UsuarioResponseDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Usuario;
import br.com.agenda.eventosapi.model.UsuarioRole;
import br.com.agenda.eventosapi.repository.UsuarioRepository;
import br.com.agenda.eventosapi.service.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public UsuarioResponseDTO registrar(RegistroDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()) != null) {
            throw new RuntimeException("Este email já está em uso.");
        }
        String encryptedPassword = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(dto.nome(), dto.email(), encryptedPassword, UsuarioRole.PARTICIPANTE);
        novoUsuario.setDataRegisto(LocalDateTime.now());
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);

        return toResponseDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(this::toResponseDTO); // O .map do Page faz a conversão para nós
    }

    @Transactional
    public UsuarioResponseDTO alterarCargo(Long utilizadorId, UsuarioRole novoCargo) {
        Usuario utilizador = usuarioRepository.findById(utilizadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado com o id: " + utilizadorId));
        utilizador.setRole(novoCargo);
        Usuario utilizadorAtualizado = usuarioRepository.save(utilizador);

        // CORRIGIDO: Usa o método mapper
        return toResponseDTO(utilizadorAtualizado);
    }

    @Transactional
    public void solicitarRedefinicaoSenha(String email) {
        Usuario usuario = (Usuario) usuarioRepository.findByEmail(email);
        if (usuario == null) {
            System.out.println("Pedido de redefinição para email (encontrado ou não): " + email);
            return;
        }
        String token = UUID.randomUUID().toString();
        LocalDateTime dataExpiracao = LocalDateTime.now().plusHours(1);
        usuario.setTokenRedefinicaoSenha(token);
        usuario.setTokenRedefinicaoExpiraEm(dataExpiracao);
        usuarioRepository.save(usuario);
        emailService.enviarEmailRedefinicaoSenha(usuario, token);
    }

    @Transactional
    public void redefinirSenha(RedefinirSenhaDTO dto) {
        Usuario usuario = usuarioRepository.findByTokenRedefinicaoSenha(dto.token())
                .orElseThrow(() -> new RuntimeException("Token de redefinição inválido."));
        if (usuario.getTokenRedefinicaoExpiraEm().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de redefinição expirado.");
        }
        usuario.setSenha(passwordEncoder.encode(dto.novaSenha()));
        usuario.setTokenRedefinicaoSenha(null);
        usuario.setTokenRedefinicaoExpiraEm(null);
        usuarioRepository.save(usuario);
    }

    private UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        String imagemUrl = null;
        if (usuario.getImagemPerfil() != null) {
            imagemUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/perfil/")
                    .path(usuario.getId().toString())
                    .path("/imagem")
                    .toUriString();
        }
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getBiografia(),
                usuario.getCidade(),
                imagemUrl
        );
    }
}
