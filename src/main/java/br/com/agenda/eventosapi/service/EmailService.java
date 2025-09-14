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
@Service
public class EmailService {
    private final Resend resend;

    public EmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Async
    public void enviarEmailConfirmacaoInscricao(Participante participante) {
        String nomeEvento = participante.getEvento().getNome();
        String dataEvento = participante.getEvento().getData().toString();

        String assunto = "Confirmação de Inscrição no Evento: " + nomeEvento;
        String corpoHtml = String.format(
                "<h1>Olá, %s!</h1>" +
                        "<p>A sua inscrição no evento <strong>%s</strong>, que acontecerá em %s, foi confirmada com sucesso!</p>" +
                        "<p>Estamos ansiosos por vê-lo lá.</p>",
                participante.getNome(), nomeEvento, dataEvento
        );

        // CLASSE CORRIGIDA: CreateEmailOptions em vez de SendEmailRequest
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("confirmacao@seudominio.com") // Deve ser um domínio verificado no Resend
                .to(participante.getEmail())
                .subject(assunto)
                .html(corpoHtml)
                .build();

        try {
            // MÉTODO CORRETO
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email de confirmação enviado com sucesso! ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }
    }
    @Async
    public void enviarEmailLembreteEvento(Evento evento) {
        if (evento.getParticipantes().isEmpty()) {
            return;
        }

        String assunto = "Lembrete: O seu evento '" + evento.getNome() + "' é amanhã!";

        for (Participante participante : evento.getParticipantes()) {
            String corpoHtml = String.format(
                    "<h1>Olá, %s!</h1>" +
                            "<p>Este é um lembrete de que o evento <strong>%s</strong> acontecerá amanhã!</p>" +
                            "<p><strong>Data:</strong> %s</p>" +
                            "<p><strong>Local:</strong> %s</p>" +
                            "<p>Esperamos por si!</p>",
                    participante.getNome(),
                    evento.getNome(),
                    evento.getData().toString(),
                    evento.getEndereco().getCidade()
            );

            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("lembretes@seudominio.com") //Altere caso queira mudar i email de dominio
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
        String assunto = "Redefinição de Senha - Plataforma de Eventos";
        // IMPORTANTE: Num projeto real, este URL base viria do application.properties
        String urlRedefinicao = "http://localhost:3000/redefinir-senha?token=" + token;

        String corpoHtml = String.format(
                "<h1>Olá, %s!</h1>" +
                        "<p>Recebemos um pedido para redefinir a sua senha. Por favor, clique no link abaixo para criar uma nova senha:</p>" +
                        "<a href=\"%s\">Redefinir Minha Senha</a>" +
                        "<p>Se não foi você que fez este pedido, por favor ignore este e-mail.</p>",
                usuario.getNome(), urlRedefinicao
        );

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("suporte@seudominio.com") // Deve ser um domínio verificado no Resend
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
