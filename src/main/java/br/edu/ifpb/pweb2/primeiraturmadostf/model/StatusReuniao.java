package br.edu.ifpb.pweb2.primeiraturmadostf.model;

public enum StatusReuniao {
    ENCERRADA("Encerrada"),
    PROGRAMADA("Programada");

    private final String descricao;

    StatusReuniao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}
