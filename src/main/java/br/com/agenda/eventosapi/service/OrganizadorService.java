package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.OrganizadorDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.repository.OrganizadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizadorService {

    @Autowired
    private OrganizadorRepository organizadorRepository;

    @Transactional(readOnly = true)
    public List<OrganizadorDTO> listarTodos() {
        return organizadorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<OrganizadorDTO> buscarPorId(Long id) {
        return organizadorRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public OrganizadorDTO salvar(OrganizadorDTO dto) {
        Organizador organizador = toEntity(dto);
        return toDTO(organizadorRepository.save(organizador));
    }

    @Transactional
    public OrganizadorDTO atualizar(Long id, OrganizadorDTO dto) {
        Organizador organizadorExistente = organizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado com o id: " + id));

        organizadorExistente.setNome(dto.nome());
        organizadorExistente.setEmail(dto.email());
        // Se o telefone precisar ser atualizado
        // organizadorExistente.setTelefone(dto.telefone());

        return toDTO(organizadorRepository.save(organizadorExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!organizadorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organizador não encontrado com o id: " + id);
        }
        organizadorRepository.deleteById(id);
    }

    // --- Mapeadores ---
    private OrganizadorDTO toDTO(Organizador organizador) {
        return new OrganizadorDTO(organizador.getId(), organizador.getNome(), organizador.getEmail());
    }

    private Organizador toEntity(OrganizadorDTO dto) {
        Organizador organizador = new Organizador();
        organizador.setId(dto.id());
        organizador.setNome(dto.nome());
        organizador.setEmail(dto.email());
        return organizador;
    }
}