package br.com.agenda.eventosapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventosApiApplication.class, args);
	}

}
