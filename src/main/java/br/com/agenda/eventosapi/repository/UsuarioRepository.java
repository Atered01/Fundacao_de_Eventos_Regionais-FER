package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Usuario;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByEmail(String email);
    long countByDataRegistoAfter(LocalDateTime data);

    Optional<Usuario> findByTokenRedefinicaoSenha(String token);
}