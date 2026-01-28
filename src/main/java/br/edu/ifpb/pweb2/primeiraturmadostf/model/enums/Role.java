package br.edu.ifpb.pweb2.primeiraturmadostf.model.enums;

public enum Role {
    ROLE_ALUNO("Aluno"),
    ROLE_PROFESSOR("Professor"),
    ROLE_COORDENADOR("Coordenador"),
    ROLE_ADMIN("Administrador");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
