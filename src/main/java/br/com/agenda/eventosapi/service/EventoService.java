package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.*;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Categoria;
import br.com.agenda.eventosapi.model.Endereco;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.repository.CategoriaRepository;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.OrganizadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private OrganizadorRepository organizadorRepository;
    @Autowired
    private EnderecoService enderecoService;

    @Transactional
    public EventoResponseDTO salvar(EventoCreateDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));
        Organizador organizador = organizadorRepository.findById(dto.organizadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado!"));

        Endereco endereco = enderecoService.findOrCreate(dto.endereco());

        Evento evento = new Evento();
        evento.setNome(dto.nome());
        evento.setDescricao(dto.descricao());
        evento.setData(dto.data());
        evento.setEndereco(endereco); // Associa o endereço obtido
        evento.setLimiteParticipantes(dto.limiteParticipantes());
        evento.setCategoria(categoria);
        evento.setOrganizador(organizador);

        Evento eventoSalvo = eventoRepository.save(evento);

        return toResponseDTO(eventoSalvo);
    }

    @Transactional(readOnly = true)
    public Optional<EventoResponseDTO> buscarPorId(Long id) {
        return eventoRepository.findById(id).map(this::toResponseDTO);
    }

    private EventoResponseDTO toResponseDTO(Evento evento) {
        CategoriaDTO categoriaDTO = new CategoriaDTO(
                evento.getCategoria().getId(),
                evento.getCategoria().getNome(),
                evento.getCategoria().getDescricao());

        OrganizadorDTO organizadorDTO = new OrganizadorDTO(
                evento.getOrganizador().getId(),
                evento.getOrganizador().getNome(),
                evento.getOrganizador().getEmail());

        List<ParticipanteDTO> participanteDTOS = evento.getParticipantes().stream()
                .map(p -> new ParticipanteDTO(p.getId(), p.getNome(), p.getEmail()))
                .collect(Collectors.toList());

        EnderecoDTO enderecoDTO = new EnderecoDTO(
                evento.getEndereco().getLogradouro(),
                evento.getEndereco().getNumero(),
                evento.getEndereco().getBairro(),
                evento.getEndereco().getCidade(),
                evento.getEndereco().getEstado(),
                evento.getEndereco().getCep()
        );

        return new EventoResponseDTO(
                evento.getId(),
                evento.getNome(),
                evento.getDescricao(),
                evento.getData(),
                evento.getImagemUrl(),
                enderecoDTO,
                categoriaDTO,
                organizadorDTO,
                participanteDTOS);
    }

    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoCreateDTO dto) {
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + id));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));
        Organizador organizador = organizadorRepository.findById(dto.organizadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado!"));

        Endereco endereco = enderecoService.findOrCreate(dto.endereco());

        eventoExistente.setNome(dto.nome());
        eventoExistente.setDescricao(dto.descricao());
        eventoExistente.setData(dto.data());
        eventoExistente.setEndereco(endereco);
        eventoExistente.setLimiteParticipantes(dto.limiteParticipantes());
        eventoExistente.setCategoria(categoria);
        eventoExistente.setOrganizador(organizador);

        Evento eventoAtualizado = eventoRepository.save(eventoExistente);
        return toResponseDTO(eventoAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento não encontrado com o id: " + id);
        }
        eventoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<EventoResponseDTO> listarEventosPaginado(Pageable pageable) {
        Page<Evento> eventosPage = eventoRepository.findAllByDataAfter(LocalDateTime.now(), pageable);

        // Converte a página de Entidades para uma página de DTOs
        return eventosPage.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarEventosProximos(BigDecimal userLat, BigDecimal userLon, Double radius) {
        List<Evento> eventos = eventoRepository.findEventosProximos(userLat, userLon, radius);
        return eventos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @jakarta.transaction.Transactional
    public EventoResponseDTO atualizarImagem(Long id, String imagemUrl) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + id));

        evento.setImagemUrl(imagemUrl);
        Evento eventoSalvo = eventoRepository.save(evento);

        return toResponseDTO(eventoSalvo);
    }

}