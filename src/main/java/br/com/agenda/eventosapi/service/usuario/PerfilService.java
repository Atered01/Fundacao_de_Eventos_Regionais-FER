package br.com.agenda.eventosapi.service.usuario;

import br.com.agenda.eventosapi.dto.evento.EventoResponseDTO;
import br.com.agenda.eventosapi.dto.evento.InscricaoResponseDTO;
import br.com.agenda.eventosapi.dto.usuario.PerfilUpdateDTO;
import br.com.agenda.eventosapi.dto.usuario.UsuarioResponseDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Usuario;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.ParticipanteRepository;
import br.com.agenda.eventosapi.repository.UsuarioRepository;
import br.com.agenda.eventosapi.service.evento.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

@Service
public class PerfilService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private ParticipanteRepository participanteRepository;
    @Autowired
    private EventoService eventoService;


    @Transactional(readOnly = true)
    public Page<EventoResponseDTO> getMeusEventos(Authentication authentication, Pageable pageable) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
       return eventoRepository.findByOrganizadorId(usuario.getId(), pageable)
                .map(evento -> eventoService.toResponseDTO(evento)); // Reutilizando o mapper
    }

    @Transactional(readOnly = true)
    public Page<InscricaoResponseDTO> getMinhasInscricoes(Authentication authentication, Pageable pageable) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return participanteRepository.findByEmail(usuario.getEmail(), pageable)
                .map(InscricaoResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO getPerfil(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        return toResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updatePerfil(Authentication authentication, PerfilUpdateDTO dto) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        if (dto.nome() != null) {
            usuario.setNome(dto.nome());
        }
        if (dto.biografia() != null) {
            usuario.setBiografia(dto.biografia());
        }
        if (dto.cidade() != null) {
            usuario.setCidade(dto.cidade());
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return toResponseDTO(usuarioAtualizado);
    }

    @Transactional
    public void salvarImagemPerfil(String emailUsuario, MultipartFile imagem) {
        Usuario usuario = (Usuario) usuarioRepository.findByEmail(emailUsuario);
        if (usuario == null) throw new ResourceNotFoundException("Utilizador não encontrado.");
        try {
            usuario.setImagemPerfil(imagem.getBytes());
            usuarioRepository.save(usuario);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar a imagem.", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] getImagemPerfil(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado com o id: " + usuarioId));
        return usuario.getImagemPerfil();
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