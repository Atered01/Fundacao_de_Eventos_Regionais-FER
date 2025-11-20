package br.com.agenda.eventosapi.service.endereco;

import br.com.agenda.eventosapi.dto.endereco.EnderecoDTO;
import br.com.agenda.eventosapi.dto.utils.NominatimResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate = new RestTemplate();
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org/search";

    public Optional<NominatimResponseDTO> getCoordinates(EnderecoDTO dto) {
        String enderecoCompleto = String.join(", ", dto.logradouro(), dto.numero(), dto.cidade(), dto.estado());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(NOMINATIM_API_URL)
                .queryParam("q", enderecoCompleto)
                .queryParam("format", "json")
                .queryParam("limit", 1);

        String url = uriBuilder.toUriString();

        try {
            NominatimResponseDTO[] responses = restTemplate.getForObject(url, NominatimResponseDTO[].class);

            if (responses != null && responses.length > 0) {
                return Optional.of(responses[0]);
            }
        } catch (Exception e) {
            System.err.println("Erro ao chamar a API de Geocoding: " + e.getMessage());
        }

        return Optional.empty();
    }
}