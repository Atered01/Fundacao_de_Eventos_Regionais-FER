package br.com.agenda.eventosapi.config;

import br.com.agenda.eventosapi.model.Usuario;
import br.com.agenda.eventosapi.model.UsuarioRole;
import br.com.agenda.eventosapi.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Verifica se o utilizador admin já existe
        if (usuarioRepository.findByEmail("admin@eventos.com") == null) {
            // Se não existir, cria o utilizador admin com senha padrão
            Usuario admin = new Usuario(
                    "Admin",
                    "admin@eventos.com",
                    passwordEncoder.encode("admin123"), // Use uma senha forte em produção!
                    UsuarioRole.ADMIN
            );
            usuarioRepository.save(admin);
            System.out.println(">>> Utilizador ADMIN padrão criado com sucesso! Email: admin@eventos.com, Senha: admin123");
        }
    }
}