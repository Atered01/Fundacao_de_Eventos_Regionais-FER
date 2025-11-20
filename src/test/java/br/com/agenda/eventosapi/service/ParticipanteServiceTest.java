package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.dto.evento.InscricaoRequestDTO;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Participante;
import br.com.agenda.eventosapi.repository.EventoRepository;
import br.com.agenda.eventosapi.repository.ParticipanteRepository;
import br.com.agenda.eventosapi.service.usuario.ParticipanteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipanteServiceTest {

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private ParticipanteService participanteService;

    @Test
    @DisplayName("Deve inscrever participante com sucesso quando evento tem vagas")
    void inscreverParticipante_ComVagas_DeveRetornarParticipanteDTO() {
        Long eventoId = 1L;
        InscricaoRequestDTO inscricaoDTO = new InscricaoRequestDTO("João", "joao@email.com");

        Evento eventoComVagas = new Evento();
        eventoComVagas.setId(eventoId);
        eventoComVagas.setLimiteParticipantes(10);
        eventoComVagas.setParticipantes(new ArrayList<>());

        Participante participanteSalvo = new Participante();
        participanteSalvo.setId(1L);
        participanteSalvo.setNome(inscricaoDTO.nome());
        participanteSalvo.setEmail(inscricaoDTO.email());
        participanteSalvo.setEvento(eventoComVagas);

        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoComVagas));
        when(participanteRepository.save(any(Participante.class))).thenReturn(participanteSalvo);

        var resultado = participanteService.inscreverParticipante(inscricaoDTO, eventoId);

        assertNotNull(resultado);
        assertEquals("João", resultado.nome());
        assertEquals("joao@email.com", resultado.email());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar inscrever em evento lotado")
    void inscreverParticipante_EmEventoLotado_DeveLancarRuntimeException() {
        Long eventoId = 1L;
        InscricaoRequestDTO inscricaoDTO = new InscricaoRequestDTO("Maria", "maria@email.com");

        Evento eventoLotado = new Evento();
        eventoLotado.setId(eventoId);
        eventoLotado.setLimiteParticipantes(0); // Evento com 0 vagas
        eventoLotado.setParticipantes(new ArrayList<>());
        when(eventoRepository.findById(eventoId)).thenReturn(Optional.of(eventoLotado));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            participanteService.inscreverParticipante(inscricaoDTO, eventoId);
        });

        assertEquals("Evento lotado!", exception.getMessage());
    }
}