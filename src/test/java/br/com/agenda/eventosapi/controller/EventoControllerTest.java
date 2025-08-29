package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.EnderecoDTO;
import br.com.agenda.eventosapi.dto.EventoCreateDTO;
import br.com.agenda.eventosapi.model.Categoria;
import br.com.agenda.eventosapi.model.Organizador;
import br.com.agenda.eventosapi.repository.CategoriaRepository;
import br.com.agenda.eventosapi.repository.EventoRepository;
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

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EventoRepository eventoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private OrganizadorRepository organizadorRepository;

    private Categoria categoriaPadrao;
    private Organizador organizadorPadrao;
    private EnderecoDTO enderecoPadrao;

    @BeforeEach
    void setup() {
        // Limpa os repositórios para garantir o isolamento dos testes
        eventoRepository.deleteAll();
        categoriaRepository.deleteAll();
        organizadorRepository.deleteAll();

        // Cria dados de suporte que serão necessários para os testes de eventos
        categoriaPadrao = categoriaRepository.save(new Categoria(null, "Teste", "Categoria de teste"));
        organizadorPadrao = organizadorRepository.save(new Organizador(null, "Organizador Teste", "org@teste.com", null));
        enderecoPadrao = new EnderecoDTO("Rua Teste", "123", "Bairro Teste", "Cidade Teste", "Estado Teste", "12345-678");
    }

    @Test
    @DisplayName("Deve criar um novo evento com sucesso quando o utilizador for ORGANIZADOR")
    @WithMockUser(roles = "ORGANIZADOR")
    void criarEvento_ComDadosValidosEUsuarioOrganizador_DeveRetornarStatusCreated() throws Exception {
        // Arrange
        EventoCreateDTO eventoDTO = new EventoCreateDTO(
                "Evento de Teste",
                "Descrição do evento",
                LocalDateTime.now().plusDays(10), // Data futura
                enderecoPadrao,
                100,
                categoriaPadrao.getId(),
                organizadorPadrao.getId()
        );
        String eventoJson = objectMapper.writeValueAsString(eventoDTO);

        // Act & Assert
        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Evento de Teste"))
                .andExpect(jsonPath("$.categoria.nome").value("Teste"));
    }

    @Test
    @DisplayName("Deve retornar 403 Forbidden ao tentar criar evento com cargo de PARTICIPANTE")
    @WithMockUser(roles = "PARTICIPANTE")
    void criarEvento_ComUsuarioParticipante_DeveRetornarForbidden() throws Exception {
        // Arrange
        EventoCreateDTO eventoDTO = new EventoCreateDTO(
                "Evento Ilegal", "Desc", LocalDateTime.now().plusDays(5),
                enderecoPadrao, 50, categoriaPadrao.getId(), organizadorPadrao.getId()
        );
        String eventoJson = objectMapper.writeValueAsString(eventoDTO);

        // Act & Assert
        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request ao tentar criar evento com data no passado")
    @WithMockUser(roles = "ORGANIZADOR")
    void criarEvento_ComDataNoPassado_DeveRetornarBadRequest() throws Exception {
        // Arrange
        EventoCreateDTO eventoDTO = new EventoCreateDTO(
                "Evento Inválido", "Desc", LocalDateTime.now().minusDays(1), // Data no passado
                enderecoPadrao, 50, categoriaPadrao.getId(), organizadorPadrao.getId()
        );
        String eventoJson = objectMapper.writeValueAsString(eventoDTO);

        // Act & Assert
        mockMvc.perform(post("/eventos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventoJson))
                .andExpect(status().isBadRequest());
    }
}