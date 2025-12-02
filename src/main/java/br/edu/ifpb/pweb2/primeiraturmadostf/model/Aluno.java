package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "aluno",
    indexes = {
        @Index(name = "idx_aluno_matricula", columnList = "matricula")
    }
)
public class Aluno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 20)
    private String matricula;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 15)
    private String fone;

    /** 
    @Column(nullable = false, unique = true, length = 50)
    private String login;*/
    
    @Column(nullable = false, length = 100)
    private String senha; // IMPORTANTE: armazenar com BCrypt no Service!
    
    @OneToMany(mappedBy = "interessado")
    private Set<Processo> processos = new HashSet<>();
    
    // Construtores
    public Aluno() {}
    
    public Aluno(String matricula, String nome, String login, String senha) {
        this.matricula = matricula;
        this.nome = nome;
        //this.login = login;
        this.senha = senha;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMatricula() {
        return matricula;
    }
    
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getFone() {
        return fone;
    }
    
    public void setFone(String fone) {
        this.fone = fone;
    }
    
    /**
    public String getLogin() {
        return login;
    } 
    
    public void setLogin(String login) {
        this.login = login;
    } */
    
    public String getSenha() {
        return senha;
    }
    
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public Set<Processo> getProcessos() {
        return processos;
    }
    
    public void setProcessos(Set<Processo> processos) {
        this.processos = processos;
    }
    
    // Métodos auxiliares
    public void addProcesso(Processo processo) {
        processos.add(processo);
        processo.setInteressado(this);
    }
    
    public void removeProcesso(Processo processo) {
        processos.remove(processo);
        processo.setInteressado(null);
    }
    
    @Override
    public String toString() {
        return "Aluno{id=" + id + ", matricula='" + matricula + "', nome='" + nome + "'}";
    }
}