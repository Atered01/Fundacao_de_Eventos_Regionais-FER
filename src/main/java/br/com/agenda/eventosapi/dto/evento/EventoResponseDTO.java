package br.com.agenda.eventosapi.dto.evento;

import br.com.agenda.eventosapi.dto.endereco.EnderecoDTO;
import br.com.agenda.eventosapi.dto.admin.OrganizadorDTO;
import br.com.agenda.eventosapi.dto.usuario.ParticipanteDTO;
import br.com.agenda.eventosapi.model.Categoria;
import br.com.agenda.eventosapi.model.Endereco;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Organizador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record EventoResponseDTO(Long id,
                                String nome,
                                String descricao,
                                String resumo,
                                LocalDateTime data,
                                String imagemUrl,
                                EnderecoDTO endereco,
                                CategoriaDTO categoria, // Retorna o DTO da categoria, não a entidade
                                OrganizadorDTO organizador, // Retorna o DTO do organizador
                                List<ParticipanteDTO> participantes) {

    public static EventoResponseDTO fromEntity(Evento evento) {

        // 1. Lógica da URL da Imagem
        String urlImagem = (evento.getId() != null) ? "/eventos/" + evento.getId() + "/imagem" : null;

        // 2. Mapeamento Manual de ENDEREÇO
        EnderecoDTO enderecoDTO = null;
        if (evento.getEndereco() != null) {
            Endereco end = evento.getEndereco();
            // ATENÇÃO: Verifique se a ordem dos campos bate com o seu EnderecoDTO
            enderecoDTO = new EnderecoDTO(
                    end.getLogradouro(),
                    end.getNumero(),
                    end.getBairro(),
                    end.getCidade(),
                    end.getEstado(),
                    end.getCep()
            );
        }

        // 3. Mapeamento Manual de CATEGORIA
        CategoriaDTO categoriaDTO = null;
        if (evento.getCategoria() != null) {
            Categoria cat = evento.getCategoria();
            categoriaDTO = new CategoriaDTO(
                    cat.getId(),
                    cat.getNome(),
                    cat.getDescricao()
            );
        }

        // 4. Mapeamento Manual de ORGANIZADOR
        OrganizadorDTO organizadorDTO = null;
        if (evento.getOrganizador() != null) {
            Organizador org = evento.getOrganizador();
            organizadorDTO = new OrganizadorDTO(
                    org.getId(),
                    org.getNome(),
                    org.getEmail()
                    // adicione outros campos se houver no DTO
            );
        }

        // 5. Mapeamento da Lista de PARTICIPANTES
        // Se a lista for nula, retorna lista vazia para não quebrar o front
        List<ParticipanteDTO> participantesDTO = List.of();
        if (evento.getParticipantes() != null) {
            participantesDTO = evento.getParticipantes().stream()
                    .map(p -> new ParticipanteDTO(
                            p.getId(),
                            p.getNome(),
                            p.getEmail()
                            // mapeie os campos do ParticipanteDTO aqui
                    ))
                    .collect(Collectors.toList());
        }

        // 6. Retorna o DTO Principal preenchido
        return new EventoResponseDTO(
                evento.getId(),
                evento.getNome(),
                evento.getDescricao(),
                evento.getResumo(),
                evento.getData(),
                urlImagem,
                enderecoDTO,
                categoriaDTO,
                organizadorDTO,
                participantesDTO
        );
    }
}