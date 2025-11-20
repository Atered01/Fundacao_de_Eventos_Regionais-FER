package br.com.agenda.eventosapi.service.utils;

import br.com.agenda.eventosapi.dto.utils.ViaCepResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ViaCepService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String VIACEP_API_URL = "https://viacep.com.br/ws/{cep}/json/";

    public Optional<ViaCepResponseDTO> consultarCep(String cep) {
        try {
            ViaCepResponseDTO response = restTemplate.getForObject(VIACEP_API_URL, ViaCepResponseDTO.class, cep);
           if (response != null && response.cep() != null) {
                return Optional.of(response);
            }
        } catch (Exception e) {
            System.err.println("Erro ao consultar a API do ViaCEP: " + e.getMessage());
        }
        return Optional.empty();
    }
}