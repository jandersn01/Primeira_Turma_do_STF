package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "curso")
public class Curso {

        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do curso é obrigatório")
    @Column(nullable = false, length = 100)
    private String nome;

   @Pattern(
    regexp = "\\d{3}",
    message = "O código do curso deve ter exatamente 3 dígitos (ex: 123)"
)
@Column(length = 3, nullable = false)
    private String codigo;

    // --- RELACIONAMENTOS ---

    @OneToMany(mappedBy = "curso")
    private Set<Aluno> alunos = new HashSet<>();

    @OneToMany(mappedBy = "curso")
    private Set<Professor> professores = new HashSet<>();

    @OneToMany(mappedBy = "curso")
    private Set<Colegiado> colegiados = new HashSet<>();

    public Curso() {
    }

    public Curso(String nome, String codigo) {
        this.nome = nome;
        this.codigo = codigo;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Set<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<Aluno> alunos) {
        this.alunos = alunos;
    }

    public Set<Professor> getProfessores() {
        return professores;
    }

    public void setProfessores(Set<Professor> professores) {
        this.professores = professores;
    }

    public Set<Colegiado> getColegiados() {
        return colegiados;
    }

    public void setColegiados(Set<Colegiado> colegiados) {
        this.colegiados = colegiados;
    }

}
