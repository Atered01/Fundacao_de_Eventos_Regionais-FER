package br.com.agenda.eventosapi.service.admin;

import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Participante;
import br.com.agenda.eventosapi.repository.EventoRepository;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private EventoRepository eventoRepository;

    public void gerarCsvInscritos(Writer writer, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + eventoId));
        List<Participante> participantes = evento.getParticipantes();

        try (CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] cabecalho = {"ID_Participante", "Nome", "Email"};
            csvWriter.writeNext(cabecalho);

            for (Participante participante : participantes) {
                String[] dados = {
                        participante.getId().toString(),
                        participante.getNome(),
                        participante.getEmail()
                };
                csvWriter.writeNext(dados);
            }
            System.out.println("Relatório CSV gerado com sucesso para o evento: " + evento.getNome());
        } catch (IOException e) {
            System.err.println("Erro ao gerar o ficheiro CSV: " + e.getMessage());
            throw new RuntimeException("Falha ao gerar o relatório CSV.", e);
        }
    }
}
