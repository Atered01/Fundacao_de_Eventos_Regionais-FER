package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.service.SummarizerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OllamaSummarizerServiceTest {

    @Autowired
    private SummarizerService summarizer;

    @Test
    void deveGerarResumo() {
        String resumo = summarizer.gerarResumo("Show de rock internacional no parque da cidade.");
        System.out.println("Resumo gerado: " + resumo);
        assertNotNull(resumo);
    }
}
