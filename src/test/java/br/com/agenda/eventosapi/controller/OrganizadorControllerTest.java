package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.admin.OrganizadorDTO;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.repository.OrganizadorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrganizadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganizadorRepository organizadorRepository;

    @BeforeEach
    void setup() {
        organizadorRepository.deleteAll();
    }

    // --- Testes de Criação (POST) ---

    @Test
    @DisplayName("Deve criar um novo organizador com sucesso e retornar status 201")
    @WithMockUser(roles = "ADMIN") // Apenas um ADMIN pode criar um novo organizador
    void criarOrganizador_ComDadosValidos_DeveRetornarStatusCreated() throws Exception {
        OrganizadorDTO organizadorDTO = new OrganizadorDTO(null, "Centro Cultural X", "contato@ccx.com");
        String organizadorJson = objectMapper.writeValueAsString(organizadorDTO);

        mockMvc.perform(post("/organizadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(organizadorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Centro Cultural X"));
    }

    // --- Testes de Leitura (GET) ---

    @Test
    @DisplayName("Deve retornar um organizador por ID com sucesso e status 200")
    @WithMockUser // Qualquer utilizador autenticado pode ver os organizadores
    void buscarOrganizadorPorId_QuandoIdExiste_DeveRetornarOrganizador() throws Exception {
        // Arrange
        Organizador organizadorSalvo = organizadorRepository.save(new Organizador(null, "Teatro Y", "contato@teatroy.com", "12345"));

        // Act & Assert
        mockMvc.perform(get("/organizadores/" + organizadorSalvo.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Teatro Y"));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao buscar um organizador por um ID que não existe")
    @WithMockUser
    void buscarOrganizadorPorId_QuandoIdNaoExiste_DeveRetornarStatusNotFound() throws Exception {
        mockMvc.perform(get("/organizadores/999"))
                .andExpect(status().isNotFound());
    }

    // --- Teste de Atualização (PUT) ---

    @Test
    @DisplayName("Deve atualizar um organizador com sucesso e retornar status 200")
    @WithMockUser(roles = "ADMIN") // Apenas um ADMIN pode editar um organizador
    void atualizarOrganizador_ComDadosValidos_DeveRetornarOrganizadorAtualizado() throws Exception {
        // Arrange
        Organizador organizadorSalvo = organizadorRepository.save(new Organizador(null, "Associação Z", "contato@z.com", "555"));
        OrganizadorDTO dadosAtualizados = new OrganizadorDTO(null, "Associação de Moradores Z", "novoemail@z.com");
        String jsonAtualizado = objectMapper.writeValueAsString(dadosAtualizados);

        // Act & Assert
        mockMvc.perform(put("/organizadores/" + organizadorSalvo.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Associação de Moradores Z"))
                .andExpect(jsonPath("$.email").value("novoemail@z.com"));
    }

    // --- Testes de Remoção (DELETE) e Segurança ---

    @Test
    @DisplayName("Deve apagar um organizador com sucesso e retornar status 204")
    @WithMockUser(roles = "ADMIN")
    void deletarOrganizador_QuandoUsuarioForAdmin_DeveRetornarNoContent() throws Exception {
        // Arrange
        Organizador organizadorSalvo = organizadorRepository.save(new Organizador(null, "Escola de Música", "musica@escola.com", "789"));

        // Act & Assert
        mockMvc.perform(delete("/organizadores/" + organizadorSalvo.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 403 Forbidden ao tentar criar organizador com cargo de PARTICIPANTE")
    @WithMockUser(roles = "PARTICIPANTE")
    void criarOrganizador_QuandoUsuarioForParticipante_DeveRetornarForbidden() throws Exception {
        OrganizadorDTO organizadorDTO = new OrganizadorDTO(null, "Organizador Ilegal", "ilegal@email.com");
        String organizadorJson = objectMapper.writeValueAsString(organizadorDTO);

        mockMvc.perform(post("/organizadores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(organizadorJson))
                .andExpect(status().isForbidden());
    }
}