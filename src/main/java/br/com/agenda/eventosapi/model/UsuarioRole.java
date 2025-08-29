package br.com.agenda.eventosapi.model;

public enum UsuarioRole {
    ADMIN("admin"),
    ORGANIZADOR("organizador"),
    PARTICIPANTE("participante");

    private String role;

    UsuarioRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
