package br.com.agenda.eventosapi.repository;

import br.com.agenda.eventosapi.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByEmail(String email);
    long countByDataRegistoAfter(LocalDateTime data);
}