package br.edu.ifpb.pweb2.primeiraturmadostf.model;

public enum TipoDecisao {
    DEFERIMENTO("Deferido", "Pedido aceito"),
    INDEFERIMENTO("Indeferido", "Pedido negado");
    
    private final String titulo;
    private final String descricao;
    
    TipoDecisao(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public String getDescricao() {
        return descricao;
    }
}
