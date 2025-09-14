package br.com.agenda.eventosapi.service;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ModerationService {

    // Lista simples de termos proibidos (pode ser expandida conforme necessário)
    private static final Set<String> PALAVRAS_PROIBIDAS = Set.of(
            "ofensivo", "violento", "ódio", "hate", "racista", "sexo explícito"
    );

    public ModerationService() { }

    public boolean validarConteudo(String texto) {
        if (texto == null || texto.isBlank()) {
            return true; // nada para moderar
        }
        String lower = texto.toLowerCase();
        for (String termo : PALAVRAS_PROIBIDAS) {
            if (lower.contains(termo)) {
                return false;
            }
        }
        return true;
    }
}