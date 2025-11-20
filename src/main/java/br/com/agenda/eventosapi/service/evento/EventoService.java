package br.com.agenda.eventosapi.service.evento;

import br.com.agenda.eventosapi.dto.admin.OrganizadorDTO;
import br.com.agenda.eventosapi.dto.endereco.EnderecoDTO;
import br.com.agenda.eventosapi.dto.evento.CategoriaDTO;
import br.com.agenda.eventosapi.dto.evento.EventoCreateDTO;
import br.com.agenda.eventosapi.dto.evento.EventoResponseDTO;
import br.com.agenda.eventosapi.dto.usuario.ParticipanteDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Categoria;
import br.com.agenda.eventosapi.model.Endereco;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.repository.CategoriaRepository;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.OrganizadorRepository;
import br.com.agenda.eventosapi.service.endereco.EnderecoService;
import br.com.agenda.eventosapi.service.ia.ModerationService;
import br.com.agenda.eventosapi.service.ia.SummarizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    @Autowired
    private ModerationService moderationService;
    @Autowired
    private SummarizerService summarizerService;

    @Transactional
    public EventoResponseDTO salvar(EventoCreateDTO dto) {
        String conteudoParaValidar = dto.nome() + " " + dto.descricao();
        if (!moderationService.validarConteudo(conteudoParaValidar)) {
            throw new RuntimeException("O conteúdo do evento foi considerado impróprio e não pode ser publicado.");
        }
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));
        Organizador organizador = organizadorRepository.findById(dto.organizadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado!"));

        Endereco endereco = enderecoService.findOrCreate(dto.endereco());
        String resumoGerado = summarizerService.gerarResumo(dto.descricao());

        Evento evento = new Evento();
        evento.setNome(dto.nome());
        evento.setDescricao(dto.descricao());
        evento.setResumo(resumoGerado);
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

    protected EventoResponseDTO toResponseDTO(Evento evento) {
        String imagemUrl = null;
        if (evento.getImagem() != null) {
            // Se o evento tiver uma imagem, construímos o URL para o endpoint que a serve
            imagemUrl = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/eventos/")
                    .path(evento.getId().toString())
                    .path("/imagem")
                    .toUriString();
        }

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
                evento.getResumo(),
                evento.getData(),
                imagemUrl,
                enderecoDTO,
                categoriaDTO,
                organizadorDTO,
                participanteDTOS);
    }

    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoCreateDTO dto) {
        String conteudoParaValidar = dto.nome() + " " + dto.descricao();

        if (!moderationService.validarConteudo(conteudoParaValidar)) {
            throw new RuntimeException("O conteúdo do evento foi considerado impróprio e não pode ser publicado.");
        }
        String resumoGerado = summarizerService.gerarResumo(dto.descricao());
        Evento eventoExistente = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + id));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada!"));
        Organizador organizador = organizadorRepository.findById(dto.organizadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado!"));

        Endereco endereco = enderecoService.findOrCreate(dto.endereco());

        eventoExistente.setNome(dto.nome());
        eventoExistente.setDescricao(dto.descricao());
        eventoExistente.setResumo(resumoGerado);
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

    @Transactional
    public void salvarImagem(Long id, MultipartFile imagem) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + id));
        try {
            evento.setImagem(imagem.getBytes());
            eventoRepository.save(evento);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao processar a imagem do evento.", e);
        }
    }

    @Transactional(readOnly = true)
    public byte[] getImagem(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + id));
        return evento.getImagem();
    }

    public Page<EventoResponseDTO> listarComFiltros(String nome, String cidade, String categoria, LocalDate data, Pageable pageable) {
        Page<Evento> eventosPage = eventoRepository.encontrarComFiltros(nome, cidade, categoria, data, pageable);
        return eventosPage.map(EventoResponseDTO::fromEntity);
    }

    public List<String> listarCidadesComEventos() {
        return eventoRepository.findCidadesDistintas();
    }
}