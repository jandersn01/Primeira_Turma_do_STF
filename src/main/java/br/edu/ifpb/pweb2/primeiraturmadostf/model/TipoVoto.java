package br.edu.ifpb.pweb2.primeiraturmadostf.model;

public enum TipoVoto {
    COM_RELATOR("Com relator"),
    DIVERGENTE("Divergente");

    private final String descricao;

    TipoVoto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
