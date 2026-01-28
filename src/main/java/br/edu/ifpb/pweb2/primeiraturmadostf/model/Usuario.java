package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "usuario",
    indexes = {
        @Index(name = "idx_usuario_matricula", columnList = "matricula")
    }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Matrícula é obrigatória")
    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false, length = 100)
    private String senha;

    @NotNull(message = "Role é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(nullable = false)
    private Boolean ativo = true;

    @OneToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @OneToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    public Usuario() {}

    public Usuario(String matricula, String senha, Role role) {
        this.matricula = matricula;
        this.senha = senha;
        this.role = role;
        this.ativo = true;
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getNome() {
        if (aluno != null) {
            return aluno.getNome();
        }
        if (professor != null) {
            return professor.getNome();
        }
        return matricula;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", matricula='" + matricula + "', role=" + role + ", ativo=" + ativo + "}";
    }
}
