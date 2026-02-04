package br.edu.ifpb.pweb2.primeiraturmadostf.model;

public enum StatusProcesso {
    CRIADO("Criado"),
    DISTRIBUIDO("Distribuído ao relator"),
    DISPONIVEL("Disponível para pauta"),  // Relator já emitiu parecer
    EM_PAUTA("Em pauta"),
    EM_JULGAMENTO("Em julgamento"),
    JULGADO("Julgado");
    
    private final String descricao;
    
    StatusProcesso(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
