package br.com.agenda.eventosapi.service.evento;

import br.com.agenda.eventosapi.dto.avaliacao.AvaliacaoCreateDTO;
import br.com.agenda.eventosapi.dto.avaliacao.AvaliacaoResponseDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Avaliacao;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Usuario;
import br.com.agenda.eventosapi.repository.AvaliacaoRepository;
import br.com.agenda.eventosapi.repository.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Transactional
    public AvaliacaoResponseDTO criar(Long eventoId, AvaliacaoCreateDTO dto) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + eventoId));

        validarPermissaoParaAvaliar(evento, usuarioLogado);

        Avaliacao novaAvaliacao = new Avaliacao();
        novaAvaliacao.setNota(dto.nota());
        novaAvaliacao.setComentario(dto.comentario());
        novaAvaliacao.setDataAvaliacao(LocalDateTime.now());
        novaAvaliacao.setEvento(evento);
        novaAvaliacao.setUsuario(usuarioLogado);

        try {
            Avaliacao avaliacaoSalva = avaliacaoRepository.save(novaAvaliacao);
            return toResponseDTO(avaliacaoSalva);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Este utilizador já avaliou este evento.");
        }
    }

    @Transactional(readOnly = true)
    public Page<AvaliacaoResponseDTO> listarPorEvento(Long eventoId, Pageable pageable) {
        if (!eventoRepository.existsById(eventoId)) {
            throw new ResourceNotFoundException("Evento não encontrado com o id: " + eventoId);
        }

        return avaliacaoRepository.findByEventoId(eventoId, pageable)
                .map(this::toResponseDTO);
    }

    private void validarPermissaoParaAvaliar(Evento evento, Usuario usuario) {
        if (evento.getData().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Não é possível avaliar um evento que ainda não aconteceu.");
        }

        boolean participou = evento.getParticipantes().stream()
                .anyMatch(p -> p.getEmail().equals(usuario.getEmail()));

        if (!participou) {
            throw new RuntimeException("Apenas participantes inscritos podem avaliar este evento.");
        }
    }

    private AvaliacaoResponseDTO toResponseDTO(Avaliacao avaliacao) {
        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getNota(),
                avaliacao.getComentario(),
                avaliacao.getDataAvaliacao(),
                avaliacao.getUsuario().getNome()
        );
    }
}