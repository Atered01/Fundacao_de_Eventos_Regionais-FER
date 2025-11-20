package br.com.agenda.eventosapi.dto.admin;

public record DashboardStatsDTO(  long totalUsuarios,
                                  long totalEventosAtivos,
                                  long totalInscricoes,
                                  long novosUsuariosUltimos7Dias) {
}
