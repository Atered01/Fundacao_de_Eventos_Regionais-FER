package br.com.agenda.eventosapi.dto.usuario;

public record UsuarioResponseDTO(Long id,
                                 String nome,
                                 String email,
                                 String biografia,
                                 String cidade,
                                 String imagemPerfilUrl) {
}