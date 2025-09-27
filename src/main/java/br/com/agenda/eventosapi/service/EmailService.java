package br.com.agenda.eventosapi.service;

import br.com.agenda.eventosapi.model.Evento;
import br.com.agenda.eventosapi.model.Participante;
import br.com.agenda.eventosapi.model.Usuario;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    private final Resend resend;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${server.port:8080}")
    private String port;
    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;
    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Async
    public void enviarEmailConfirmacaoInscricao(Participante participante) {
        Evento evento = participante.getEvento();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy 'às' HH:mm'h'");
        String dataFormatada = evento.getData().format(formatter);

        String localCompleto = String.format("%s, %s - %s, %s",
                evento.getEndereco().getLogradouro(),
                evento.getEndereco().getNumero(),
                evento.getEndereco().getCidade(),
                evento.getEndereco().getEstado());

        String baseUrl = "http://localhost:" + port + contextPath;
        String urlCalendario = baseUrl + "/eventos/" + evento.getId() + "/exportar-ical";
        String urlVerEvento = "http://localhost:3000/eventos/" + evento.getId();


        String assunto = "Inscrição Confirmada: " + evento.getNome();
        String corpoHtml = String.format("""
            <div style="font-family: Arial, sans-serif; color: #333;">
                <h2>Olá, %s!</h2>
                <p>A sua inscrição no evento <strong>%s</strong> foi confirmada com sucesso!</p>
                <hr>
                <h3>Detalhes do Evento:</h3>
                <ul>
                    <li><strong>Data e Hora:</strong> %s</li>
                    <li><strong>Local:</strong> %s</li>
                    <li><strong>Organizado por:</strong> %s</li>
                </ul>
                <hr>
                <p style="text-align: center;">
                    <a href="%s" style="background-color: #007bff; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px; margin-right: 10px;">Adicionar ao Calendário</a>
                    <a href="%s" style="background-color: #6c757d; color: white; padding: 10px 15px; text-decoration: none; border-radius: 5px;">Ver no Site</a>
                </p>
                <p>Estamos ansiosos por vê-lo lá!</p>
            </div>
            """,
                participante.getNome(),
                evento.getNome(),
                dataFormatada,
                localCompleto,
                evento.getOrganizador().getNome(),
                urlCalendario,
                urlVerEvento
        );

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to(participante.getEmail())
                .subject(assunto)
                .html(corpoHtml)
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            System.err.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }
    }
    @Async
    public void enviarEmailLembreteEvento(Evento evento) {
        if (evento.getParticipantes().isEmpty()) return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm'h'");
        String dataFormatada = evento.getData().format(formatter);
        String localCompleto = String.format("%s, %s, %s",
                evento.getEndereco().getLogradouro(),
                evento.getEndereco().getNumero(),
                evento.getEndereco().getCidade());

        String assunto = "Lembrete: O seu evento '" + evento.getNome() + "' é amanhã!";

        for (Participante participante : evento.getParticipantes()) {
            String corpoHtml = String.format("""
                <div style="font-family: Arial, sans-serif; color: #333;">
                    <h2>Olá, %s!</h2>
                    <p>Este é um lembrete amigável de que o evento <strong>%s</strong> acontecerá amanhã.</p>
                    <p><strong>Data:</strong> %s</p>
                    <p><strong>Local:</strong> %s</p>
                    <p>Esperamos por si!</p>
                </div>
                """,
                    participante.getNome(),
                    evento.getNome(),
                    dataFormatada,
                    localCompleto
            );

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("onboarding@resend.dev")
                    .to(participante.getEmail())
                    .subject(assunto)
                    .html(corpoHtml)
                    .build();

            try {
                resend.emails().send(params);
            } catch (ResendException e) {
                System.err.printf("Falha ao enviar lembrete para %s: %s%n", participante.getEmail(), e.getMessage());
            }
        }
    }

    @Async
    public void enviarEmailRedefinicaoSenha(Usuario usuario, String token) {
        String assunto = "Redefinição de Senha - FER (Fundação de Eventos Regionais)";

        // Constrói o URL de forma dinâmica usando a propriedade
        String urlRedefinicao = frontendBaseUrl + "/redefinir-senha?token=" + token;

        String corpoHtml = String.format("""
            <div style="font-family: Arial, sans-serif; color: #333; line-height: 1.6;">
                <h2>Olá, %s!</h2>
                <p>Recebemos uma solicitação para redefinir a senha da sua conta na plataforma <strong>FER - Fundação de Eventos Regionais</strong>.</p>
                <p>Se foi você que solicitou, por favor, clique no botão abaixo para criar uma nova senha. Este link é válido por 1 hora.</p>
                <p style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #dc3545; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-size: 16px;">Redefinir Minha Senha</a>
                </p>
                <p>Se não foi você que fez este pedido, pode ignorar este e-mail com segurança. Nenhuma alteração será feita na sua conta.</p>
                <hr>
                <p style="font-size: 12px; color: #6c757d;">Atenciosamente,<br>Equipa da FER</p>
            </div>
            """,
                usuario.getNome(), urlRedefinicao
        );

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev") // Lembre-se de usar um domínio verificado
                .to(usuario.getEmail())
                .subject(assunto)
                .html(corpoHtml)
                .build();

        try {
            resend.emails().send(params);
            System.out.println("Email de redefinição de senha enviado para: " + usuario.getEmail());
        } catch (ResendException e) {
            System.err.println("Erro ao enviar email de redefinição: " + e.getMessage());
        }
    }
}
