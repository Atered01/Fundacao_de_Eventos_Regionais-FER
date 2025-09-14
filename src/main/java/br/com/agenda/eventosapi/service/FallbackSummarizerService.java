package br.com.agenda.eventosapi.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
// Este serviço será criado se a propriedade for 'false' OU se não existir de todo
@ConditionalOnProperty(name = "spring.ai.ollama.enabled", havingValue = "false", matchIfMissing = true)
public class FallbackSummarizerService implements SummarizerService {

    public FallbackSummarizerService() {
        System.out.println(">>> Módulo de IA com Ollama DESATIVADO. Usando fallback para resumos.");
    }

    @Override
    public String gerarResumo(String descricaoCompleta) {
        // Lógica de fallback muito simples: apenas corta o texto.
        if (descricaoCompleta == null) return null;
        return descricaoCompleta.substring(0, Math.min(descricaoCompleta.length(), 150)) + "...";
    }
}