package br.com.agenda.eventosapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API sem estado
                .authorizeHttpRequests(authorize -> authorize
                        // Login, Cadastro e Esquici senha
                        .requestMatchers(HttpMethod.POST, "/auth/registrar", "/auth/login", "/auth/esqueci-senha", "/auth/redefinir-senha").permitAll()

                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Endpoint de Admin
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Permissões para Eventos (agora consistente e com acesso GET público)
                        .requestMatchers(HttpMethod.GET, "/eventos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/eventos").hasAnyRole("ADMIN", "ORGANIZADOR") // Ajustado para consistência
                        .requestMatchers(HttpMethod.PUT, "/eventos/*").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.DELETE, "/eventos/*").hasAnyRole("ADMIN", "ORGANIZADOR")

                        //Permissões para o Ranking
                        .requestMatchers(HttpMethod.GET, "/rankings/**").permitAll()

                        //Permissões para o ViaCep
                        .requestMatchers(HttpMethod.GET, "/enderecos/**").permitAll()

                        // Permissões para Avaliações
                        .requestMatchers(HttpMethod.GET, "/eventos/*/avaliacoes").permitAll() // Leitura pública
                        .requestMatchers(HttpMethod.POST, "/eventos/*/avaliacoes").hasRole("PARTICIPANTE")

                        // Permissões para Organizadores
                        .requestMatchers(HttpMethod.POST, "/organizadores").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/organizadores/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/organizadores/*").hasRole("ADMIN")

                        // Permissões para Categorias
                        .requestMatchers(HttpMethod.POST, "/categorias").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.PUT, "/categorias/*").hasAnyRole("ADMIN", "ORGANIZADOR")
                        .requestMatchers(HttpMethod.DELETE, "/categorias/*").hasAnyRole("ADMIN", "ORGANIZADOR")

                        // Permissões para Inscrições
                        .requestMatchers(HttpMethod.POST, "/eventos/*/participantes/inscrever").hasRole("PARTICIPANTE")
                        .requestMatchers(HttpMethod.DELETE, "/eventos/*/participantes/*").hasRole("PARTICIPANTE")

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}