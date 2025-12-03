package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;

@Entity
@Table(
    name = "professor",
    indexes = {
        @Index(name = "idx_professor_login", columnList = "matricula")
    }
)
public class Professor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ\\s]+$", message = "Nome deve conter apenas letras e espaços")
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
    @Pattern(regexp = "^\\(?[0-9]{2}\\)?[\\s-]?[0-9]{4,5}-?[0-9]{4}$|^$", message = "Telefone inválido. Use o formato (XX) XXXXX-XXXX ou deixe em branco")
    @Column(length = 15)
    private String fone;
    
    @NotBlank(message = "Matrícula é obrigatória")
    @Size(min = 3, max = 50, message = "Matrícula deve ter entre 3 e 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Matrícula deve conter apenas letras e números")
    @Column(nullable = false, unique = true, length = 50)
    private String matricula;
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String senha; // Armazenar com BCrypt!

    @ManyToOne
    @JoinColumn(name = "id_curso", nullable = false)
    private Curso curso;
    
    @Column(nullable = false)
    private Boolean coordenador = false; // Default false
    
    // Relacionamento: Professor é membro de vários colegiados
    @ManyToMany(mappedBy = "membros")
    private Set<Colegiado> colegiados = new HashSet<>();
    
    // Relacionamento: Professor é relator de vários processos
    @OneToMany(mappedBy = "relator")
    private Set<Processo> processosRelator = new HashSet<>();
    
    /*  Relacionamento: Professor vota em vários processos
    @OneToMany(mappedBy = "professor")
    private Set<Voto> votos = new HashSet<>();
    - De acordo com o diagrama de classes, o voto depende de professor.
    - O professor não depende de voto, portanto não existe esse relacionamento. Por mais que ele faça sentido
    */
    
    /** Relacionamento: Professor participa de várias reuniões
    @ManyToMany(mappedBy = "membros")
    private Set<Reuniao> reunioes = new HashSet<>();
    - De acordo com o diagrama de classes, o professor. não se relaciona diretamnte com reunião
    - Uma reunião agrega um processo, que tem um professor relator.
    - Reunião, por sua vez, é agregada em colegiado.
    **/
    
    // Construtores
    public Professor() {}
    
    public Professor(String nome, String matricula, String senha, Boolean coordenador) {
        this.nome = nome;
        this.matricula = matricula;
        this.senha = senha;
        this.coordenador = coordenador;
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
    
    public String getFone() {
        return fone;
    }
    
    public void setFone(String fone) {
        this.fone = fone;
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
    
    public Boolean getCoordenador() {
        return coordenador;
    }
    
    public void setCoordenador(Boolean coordenador) {
        this.coordenador = coordenador;
    }
    
    public Set<Colegiado> getColegiados() {
        return colegiados;
    }
    
    public void setColegiados(Set<Colegiado> colegiados) {
        this.colegiados = colegiados;
    }
    
    public Set<Processo> getProcessosRelator() {
        return processosRelator;
    }
    
    public void setProcessosRelator(Set<Processo> processosRelator) {
        this.processosRelator = processosRelator;
    }
    
    /** 
    public Set<Voto> getVotos() {
        return votos;
    }
    
    public void setVotos(Set<Voto> votos) {
        this.votos = votos;
    }
        */
    
    /** 
    public Set<Reuniao> getReunioes() {
        return reunioes;
    }
    
    public void setReunioes(Set<Reuniao> reunioes) {
        this.reunioes = reunioes;
    }
    **/
    
    // Métodos auxiliares
    public void addColegiado(Colegiado colegiado) {
        colegiados.add(colegiado);
        colegiado.getMembros().add(this);
    }
    
    public void removeColegiado(Colegiado colegiado) {
        colegiados.remove(colegiado);
        colegiado.getMembros().remove(this);
    }
    
    public void addProcessoRelator(Processo processo) {
        processosRelator.add(processo);
        processo.setRelator(this);
    }
    
    public void removeProcessoRelator(Processo processo) {
        processosRelator.remove(processo);
        processo.setRelator(null);
    }
    
    @Override
    public String toString() {
        return "Professor{id=" + id + ", nome='" + nome + "', coordenador=" + coordenador + "}";
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}