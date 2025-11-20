package br.com.agenda.eventosapi.service.ia;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "spring.ai.ollama.enabled", havingValue = "true")
public class OllamaSummarizerService implements SummarizerService {

    private final OllamaChatModel chatClient;

    public OllamaSummarizerService(OllamaChatModel chatClient) {
        this.chatClient = chatClient;
        System.out.println(">>> Módulo de IA com Ollama ATIVADO.");
    }

    @Override
    public String gerarResumo(String descricaoCompleta) {
        if (descricaoCompleta == null || descricaoCompleta.isBlank()) {
            return null;
        }
        try {
            String prompt = String.format(
                    "Resuma a seguinte descrição de evento em uma única frase curta e apelativa para usar num cartão de evento em português brasileiro: \"%s\"",
                    descricaoCompleta
            );

            return chatClient.call(prompt);
        } catch (Exception e) {
            System.err.println("Erro ao chamar a API do Ollama: " + e.getMessage());
            return descricaoCompleta.substring(0, Math.min(descricaoCompleta.length(), 150)) + "...";
        }
    }
}