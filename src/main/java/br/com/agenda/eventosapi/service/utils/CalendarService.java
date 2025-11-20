package br.com.agenda.eventosapi.service.utils;

import br.com.agenda.eventosapi.exception.ResourceNotFoundException;
import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.repository.EventoRepository;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.UUID;

@Service
public class CalendarService {

    @Autowired
    private EventoRepository eventoRepository;

    public void gerarArquivoIcsParaEvento(Long eventoId, Writer writer) throws IOException {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com o id: " + eventoId));

        java.util.Calendar eventDate = GregorianCalendar.from(
                evento.getData().atZone(ZoneId.systemDefault())
        );

        VEvent calendarEvent = new VEvent(new DateTime(eventDate.getTime()), evento.getNome());

        calendarEvent.getProperties().add(new Uid(UUID.randomUUID().toString()));

        calendarEvent.getProperties().add(new Description(evento.getDescricao()));
        String localCompleto = String.format("%s, %s, %s",
                evento.getEndereco().getLogradouro(),
                evento.getEndereco().getNumero(),
                evento.getEndereco().getCidade());
        calendarEvent.getProperties().add(new Location(localCompleto));

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Agenda de Eventos API//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        calendar.getComponents().add(calendarEvent);

        net.fortuna.ical4j.data.CalendarOutputter outputter = new net.fortuna.ical4j.data.CalendarOutputter();
        outputter.output(calendar, writer);
    }
}