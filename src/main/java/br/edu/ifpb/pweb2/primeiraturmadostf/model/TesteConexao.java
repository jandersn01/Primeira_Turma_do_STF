package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;

@Entity
@Table(name = "teste_conexao")
public class TesteConexao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String mensagem;
    
    // Construtor vazio (obrigatório pro JPA)
    public TesteConexao() {
    }
    
    // Construtor com parâmetros
    public TesteConexao(String mensagem) {
        this.mensagem = mensagem;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMensagem() {
        return mensagem;
    }
    
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    
    @Override
    public String toString() {
        return "TesteConexao{id=" + id + ", mensagem='" + mensagem + "'}";
    }
}