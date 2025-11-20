package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.evento.CategoriaDTO;
import br.com.agenda.eventosapi.model.Categoria;
import br.com.agenda.eventosapi.repository.CategoriaRepository;
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
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Limpa a base de dados de teste antes de cada teste para garantir isolamento
    @BeforeEach
    void setup() {
        categoriaRepository.deleteAll();
    }


    // --- Testes de Criação (POST) ---

    @Test
    @DisplayName("Deve criar uma nova categoria com sucesso e retornar status 201")
    @WithMockUser(roles = "ORGANIZADOR")
    void criarCategoria_ComDadosValidos_DeveRetornarStatusCreated() throws Exception {
        CategoriaDTO categoriaDTO = new CategoriaDTO(null, "Música", "Eventos musicais");
        String categoriaJson = objectMapper.writeValueAsString(categoriaDTO);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Música"));
    }

    @Test
    @DisplayName("Deve falhar ao tentar criar categoria com nome em branco e retornar status 400")
    @WithMockUser(roles = "ORGANIZADOR")
    void criarCategoria_ComNomeEmBranco_DeveRetornarStatusBadRequest() throws Exception {
        CategoriaDTO categoriaDTO = new CategoriaDTO(null, "", "Descrição inválida");
        String categoriaJson = objectMapper.writeValueAsString(categoriaDTO);

        mockMvc.perform(post("/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoriaJson))
                .andExpect(status().isBadRequest());
    }

    // --- Testes de Leitura (GET) ---

    @Test
    @DisplayName("Deve retornar uma categoria por ID com sucesso e status 200")
    @WithMockUser // Apenas autenticação é necessária para ler
    void buscarPorId_QuandoIdExiste_DeveRetornarCategoria() throws Exception {
        // Arrange: Primeiro, criamos uma categoria para ter o que buscar
        Categoria categoriaSalva = categoriaRepository.save(new Categoria(null, "Teatro", "Peças teatrais"));

        // Act & Assert
        mockMvc.perform(get("/categorias/" + categoriaSalva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Teatro"));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao buscar por um ID que não existe")
    @WithMockUser
    void buscarPorId_QuandoIdNaoExiste_DeveRetornarStatusNotFound() throws Exception {
        mockMvc.perform(get("/categorias/999"))
                .andExpect(status().isNotFound());
    }

    // --- Teste de Atualização (PUT) ---

    @Test
    @DisplayName("Deve atualizar uma categoria com sucesso e retornar status 200")
    @WithMockUser(roles = "ORGANIZADOR")
    void atualizarCategoria_ComDadosValidos_DeveRetornarCategoriaAtualizada() throws Exception {
        // Arrange
        Categoria categoriaSalva = categoriaRepository.save(new Categoria(null, "Arte", "Desc original"));
        CategoriaDTO dadosAtualizados = new CategoriaDTO(null, "Artes Plásticas", "Nova descrição");
        String jsonAtualizado = objectMapper.writeValueAsString(dadosAtualizados);

        // Act & Assert
        mockMvc.perform(put("/categorias/" + categoriaSalva.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAtualizado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Artes Plásticas"))
                .andExpect(jsonPath("$.descricao").value("Nova descrição"));
    }

    // --- Testes de Remoção (DELETE) e Segurança ---

    @Test
    @DisplayName("Deve apagar uma categoria com sucesso e retornar status 204")
    @WithMockUser(roles = "ORGANIZADOR")
    void deletarCategoria_QuandoUsuarioForOrganizador_DeveRetornarNoContent() throws Exception {
        // Arrange
        Categoria categoriaSalva = categoriaRepository.save(new Categoria(null, "Cinema", "Filmes"));

        // Act & Assert
        mockMvc.perform(delete("/categorias/" + categoriaSalva.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 403 Forbidden ao tentar apagar categoria com cargo de PARTICIPANTE")
    @WithMockUser(roles = "PARTICIPANTE") // Testa a regra de segurança
    void deletarCategoria_QuandoUsuarioForParticipante_DeveRetornarForbidden() throws Exception {
        // Arrange
        Categoria categoriaSalva = categoriaRepository.save(new Categoria(null, "Dança", "Apresentações"));

        // Act & Assert
        mockMvc.perform(delete("/categorias/" + categoriaSalva.getId()))
                .andExpect(status().isForbidden());
    }
}