package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.EnderecoDTO;
import br.com.agenda.eventosapi.dto.NominatimResponseDTO;
import br.com.agenda.eventosapi.model.Endereco;
import br.com.agenda.eventosapi.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private GeocodingService geocodingService;

    @Transactional
    public Endereco findOrCreate(EnderecoDTO dto) {
        return enderecoRepository.findByLogradouroAndNumeroAndCidade(
                        dto.logradouro(), dto.numero(), dto.cidade())
                .orElseGet(() -> {
                    Endereco novoEndereco = new Endereco();
                    novoEndereco.setLogradouro(dto.logradouro());
                    novoEndereco.setNumero(dto.numero());
                    novoEndereco.setBairro(dto.bairro());
                    novoEndereco.setCidade(dto.cidade());
                    novoEndereco.setEstado(dto.estado());
                    novoEndereco.setCep(dto.cep());
                    Optional<NominatimResponseDTO> coordinates = geocodingService.getCoordinates(dto);

                    coordinates.ifPresent(coords -> {
                        novoEndereco.setLatitude(coords.latitude());
                        novoEndereco.setLongitude(coords.longitude());
                    });

                    return enderecoRepository.save(novoEndereco);
                });
    }
}