package br.com.agenda.eventosapi.controller;

import br.com.agenda.eventosapi.dto.utils.ViaCepResponseDTO;
import br.com.agenda.eventosapi.service.utils.ViaCepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
@Tag(name = "Endereços", description = "Endpoints de utilidades para endereços")
public class EnderecoController {

    @Autowired
    private ViaCepService viaCepService;

    @Operation(summary = "Consulta um CEP",
            description = "Busca os detalhes de um endereço a partir de um CEP na API do ViaCEP. Endpoint público.")
    @GetMapping("/cep/{cep}")
    public ResponseEntity<ViaCepResponseDTO> consultarCep(@PathVariable String cep) {
        return viaCepService.consultarCep(cep)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}