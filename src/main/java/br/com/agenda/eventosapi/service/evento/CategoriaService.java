package br.com.agenda.eventosapi.service.evento;

import br.com.agenda.eventosapi.dto.evento.CategoriaDTO;
import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Categoria;
import br.com.agenda.eventosapi.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaDTO> buscarPorId(Long id) {
        return categoriaRepository.findById(id).map(this::toDTO);
    }

    @Transactional
    public CategoriaDTO salvar(CategoriaDTO dto) {
        Categoria categoria = toEntity(dto);
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return toDTO(categoriaSalva);
    }

    @Transactional
    public void deletar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada para o id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    @Transactional
    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada para o id: " + id));

        categoriaExistente.setNome(dto.nome());
        categoriaExistente.setDescricao(dto.descricao());
        Categoria categoriaAtualizada = categoriaRepository.save(categoriaExistente);
        return toDTO(categoriaAtualizada);
    }

    private CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome(), categoria.getDescricao());
    }

    private Categoria toEntity(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNome(dto.nome());
        categoria.setDescricao(dto.descricao());
        return categoria;
    }
}