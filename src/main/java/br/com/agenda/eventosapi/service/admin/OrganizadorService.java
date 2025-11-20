package br.com.agenda.eventosapi.service.admin;

import br.com.agenda.eventosapi.dto.admin.OrganizadorDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.repository.OrganizadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrganizadorService {

    @Autowired
    private OrganizadorRepository organizadorRepository;

    @Transactional(readOnly = true)
    public Page<OrganizadorDTO> listarTodos(Pageable pageable) {
        return organizadorRepository.findAll(pageable)
                .map(this::toDTO);
    }
    @Transactional(readOnly = true)
    public Optional<OrganizadorDTO> buscarPorId(Long id) {
        return organizadorRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public OrganizadorDTO salvar(OrganizadorDTO dto) {
        Organizador organizador = new Organizador();
        organizador.setNome(dto.nome());
        organizador.setEmail(dto.email());
        organizador = organizadorRepository.save(organizador);

        return toDTO(organizador);
    }

    @Transactional
    public OrganizadorDTO atualizar(Long id, OrganizadorDTO dto) {
        Organizador organizadorExistente = organizadorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organizador não encontrado com o id: " + id));
        organizadorExistente.setNome(dto.nome());
        organizadorExistente.setEmail(dto.email());
        return toDTO(organizadorRepository.save(organizadorExistente));
    }

    @Transactional
    public void deletar(Long id) {
        if (!organizadorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Organizador não encontrado com o id: " + id);
        }
        organizadorRepository.deleteById(id);
    }

    private OrganizadorDTO toDTO(Organizador organizador) {
        return new OrganizadorDTO(
                organizador.getId(),
                organizador.getNome(),
                organizador.getEmail()
                // Adicione telefone/descrição se seu OrganizadorDTO tiver esses campos
        );
    }
}