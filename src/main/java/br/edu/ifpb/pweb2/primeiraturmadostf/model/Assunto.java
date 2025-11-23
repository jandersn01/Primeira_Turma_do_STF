package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Assunto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String nome;
    
    @OneToMany(mappedBy = "assunto")
    private Set<Processo> processos = new HashSet<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;
    
    // Construtores
    public Assunto() {}
    
    public Assunto(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public Set<Processo> getProcessos() {
        return processos;
    }
    
    public void setProcessos(Set<Processo> processos) {
        this.processos = processos;
    }
    
    public void addProcesso(Processo processo) {
        processos.add(processo);
        processo.setAssunto(this);
    }
    
    public void removeProcesso(Processo processo) {
        processos.remove(processo);
        processo.setAssunto(null);
    }
    
    @Override
    public String toString() {
        return "Assunto{id=" + id + ", nome='" + nome + "'}";
    }
}