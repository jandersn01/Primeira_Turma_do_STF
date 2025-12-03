package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    
    @NotBlank(message = "Matrícula é obrigatória")
    @Size(min = 5, max = 20, message = "Matrícula deve ter entre 5 e 20 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Matrícula deve conter apenas letras e números")
    @Column(nullable = false, unique = true, length = 20)
    private String matricula;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras e espaços")
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    @Pattern(regexp = "^\\(?[0-9]{2}\\)?[\\s-]?[0-9]{4,5}-?[0-9]{4}$|^$", message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX ou deixe em branco")
    @Column(length = 15)
    private String fone;

    /** 
    @Column(nullable = false, unique = true, length = 50)
    private String login;*/
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String senha; // IMPORTANTE: armazenar com BCrypt no Service!
    
    @OneToMany(mappedBy = "interessado")
    private Set<Processo> processos = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    @NotNull(message = "O curso é obrigatório")
    private Curso curso;
    
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

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}