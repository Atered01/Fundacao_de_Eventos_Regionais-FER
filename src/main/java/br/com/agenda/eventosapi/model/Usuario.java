package br.com.agenda.eventosapi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity(name = "Usuario")
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true) // O email deve ser único
    private String email;

    private String senha;

    @Enumerated(EnumType.STRING) // Guarda o nome do enum ("ADMIN") como texto no banco
    private UsuarioRole role;

    @Column(name = "token_redefinicao_senha")
    private String tokenRedefinicaoSenha;

    @Column(name = "token_redefinicao_expira_em")
    private LocalDateTime tokenRedefinicaoExpiraEm;

    @Column(name = "data_registo")
    private LocalDateTime dataRegisto;

    @OneToMany(mappedBy = "usuario")
    private List<Avaliacao> avaliacoes;

    private String biografia;
    private String cidade;

    @Column(name = "imagem_perfil", columnDefinition = "MEDIUMBLOB")
    private byte[] imagemPerfil;

    public Usuario(String nome, String email, String senha, UsuarioRole role) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Agora retornamos o cargo do utilizador
        if (this.role == UsuarioRole.ADMIN) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ORGANIZADOR"), new SimpleGrantedAuthority("ROLE_PARTICIPANTE"));
        } else if (this.role == UsuarioRole.ORGANIZADOR) {
            return List.of(new SimpleGrantedAuthority("ROLE_ORGANIZADOR"), new SimpleGrantedAuthority("ROLE_PARTICIPANTE"));
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_PARTICIPANTE"));
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        // Usaremos o email como nome de utilizador para o login
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // A conta não expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // A conta não está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // As credenciais não expiram
    }

    @Override
    public boolean isEnabled() {
        return true; // A conta está ativa
    }
}
