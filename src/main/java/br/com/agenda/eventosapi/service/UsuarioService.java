package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.auth.RegistroDTO;
import br.com.agenda.eventosapi.dto.auth.UsuarioResponseDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Usuario;
import br.com.agenda.eventosapi.model.UsuarioRole;
import br.com.agenda.eventosapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO registrar(RegistroDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()) != null) {
            throw new RuntimeException("Este email já está em uso.");
        }
        String encryptedPassword = passwordEncoder.encode(dto.senha());
        Usuario novoUsuario = new Usuario(dto.nome(), dto.email(), encryptedPassword, UsuarioRole.PARTICIPANTE);
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return new UsuarioResponseDTO(usuarioSalvo.getId(), usuarioSalvo.getNome(), usuarioSalvo.getEmail());
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponseDTO alterarCargo(Long utilizadorId, UsuarioRole novoCargo) {
        Usuario utilizador = usuarioRepository.findById(utilizadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado com o id: " + utilizadorId));

        utilizador.setRole(novoCargo);
        Usuario utilizadorAtualizado = usuarioRepository.save(utilizador);

        return new UsuarioResponseDTO(utilizadorAtualizado.getId(), utilizadorAtualizado.getNome(), utilizadorAtualizado.getEmail());
    }
}
