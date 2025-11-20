package br.com.agenda.eventosapi.dto.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NominatimResponseDTO(
        @JsonProperty("lat") BigDecimal latitude,
        @JsonProperty("lon") BigDecimal longitude
) {
}