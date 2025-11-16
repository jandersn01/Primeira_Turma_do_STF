package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "colegiado",
    indexes = {
        @Index(name = "idx_colegiado_curso", columnList = "curso")
    }
)
public class Colegiado {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;
    
    @Column(name = "data_fim")
    private LocalDate dataFim; // Pode ser null se ainda estiver ativo
    
    @Column(nullable = false, length = 50)
    private String portaria; // Ex: "Portaria 123/2024"
    
    @Column(nullable = false, length = 100)
    private String curso; // Ex: "Sistemas para Internet"
    
    // Relacionamento: Colegiado tem vários membros (Professores)
    @ManyToMany
    @JoinTable(
        name = "colegiado_professor",
        joinColumns = @JoinColumn(name = "colegiado_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private Set<Professor> membros = new HashSet<>();
    
    // Relacionamento: Colegiado tem várias reuniões
    // ISSO FALTAVA NO DIAGRAMA!
    @OneToMany(mappedBy = "colegiado", cascade = CascadeType.ALL)
    private Set<Reuniao> reunioes = new HashSet<>();
    
    // Construtores
    public Colegiado() {}
    
    public Colegiado(LocalDate dataInicio, String portaria, String curso) {
        this.dataInicio = dataInicio;
        this.portaria = portaria;
        this.curso = curso;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public LocalDate getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
    
    public String getPortaria() {
        return portaria;
    }
    
    public void setPortaria(String portaria) {
        this.portaria = portaria;
    }
    
    public String getCurso() {
        return curso;
    }
    
    public void setCurso(String curso) {
        this.curso = curso;
    }
    
    public Set<Professor> getMembros() {
        return membros;
    }
    
    public void setMembros(Set<Professor> membros) {
        this.membros = membros;
    }
    
    public Set<Reuniao> getReunioes() {
        return reunioes;
    }
    
    public void setReunioes(Set<Reuniao> reunioes) {
        this.reunioes = reunioes;
    }
    
    // Métodos auxiliares
    public void addMembro(Professor professor) {
        membros.add(professor);
        professor.getColegiados().add(this);
    }
    
    public void removeMembro(Professor professor) {
        membros.remove(professor);
        professor.getColegiados().remove(this);
    }
    
    public void addReuniao(Reuniao reuniao) {
        reunioes.add(reuniao);
        reuniao.setColegiado(this);
    }
    
    public void removeReuniao(Reuniao reuniao) {
        reunioes.remove(reuniao);
        reuniao.setColegiado(null);
    }
    
    
    @Override
    public String toString() {
        return "Colegiado{id=" + id + ", curso='" + curso + "', portaria='" + portaria + "'}";
    }
}