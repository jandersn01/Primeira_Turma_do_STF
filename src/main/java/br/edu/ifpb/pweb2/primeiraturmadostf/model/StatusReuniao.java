package br.edu.ifpb.pweb2.primeiraturmadostf.model;

public enum StatusReuniao {
    PROGRAMADA("Programada"),
    EM_ANDAMENTO("Em Andamento"),
    ENCERRADA("Encerrada");

    private final String descricao;

    StatusReuniao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
