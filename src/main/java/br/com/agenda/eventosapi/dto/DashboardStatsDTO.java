package br.com.agenda.eventosapi.dto;

public record DashboardStatsDTO(  long totalUsuarios,
                                  long totalEventosAtivos,
                                  long totalInscricoes,
                                  long novosUsuariosUltimos7Dias) {
}
