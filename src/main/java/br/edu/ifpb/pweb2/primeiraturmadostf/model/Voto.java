package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "voto",
    indexes = {
        @Index(name = "idx_voto_processo", columnList = "processo_id"),
        @Index(name = "idx_voto_reuniao", columnList = "reuniao_id"),
        @Index(name = "idx_voto_professor", columnList = "professor_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_voto_professor_processo_reuniao",
            columnNames = {"professor_id", "processo_id", "reuniao_id"}
        )
    }
)
public class Voto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoVoto tipo; // COM_RELATOR ou DIVERGENTE
    
  
    @Column(nullable = false)
    private Boolean ausente = false;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String justificativa; // Texto opcional explicando o voto
    
    @Column(name = "data_voto")
    private LocalDateTime dataVoto; // Quando o voto foi registrado
    
    // Relacionamentos
    
    // adicionado: faltava no diagrama -quem votou?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;
    
    // faltava no diagrama - em qual reunião foi o voto?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reuniao_id", nullable = false)
    private Reuniao reuniao;
    
    // Construtores
    
    public Voto() {}
    
    public Voto(Professor professor, TipoVoto tipo, Processo processo, Reuniao reuniao) {
        this.professor = professor;
        this.tipo = tipo;
        this.processo = processo;
        this.reuniao = reuniao;
        this.ausente = false;
        this.dataVoto = LocalDateTime.now();
    }
    
    // Construtor para voto ausente
    public Voto(Professor professor, Processo processo, Reuniao reuniao) {
        this.professor = professor;
        this.processo = processo;
        this.reuniao = reuniao;
        this.ausente = true;
        this.tipo = null; 
        this.dataVoto = LocalDateTime.now();
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public TipoVoto getTipo() {
        return tipo;
    }
    
    public void setTipo(TipoVoto tipo) {
        this.tipo = tipo;
    }
    
    public Boolean getAusente() {
        return ausente;
    }
    
    public void setAusente(Boolean ausente) {
        this.ausente = ausente;
    }
    
    public String getJustificativa() {
        return justificativa;
    }
    
    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
    
    public LocalDateTime getDataVoto() {
        return dataVoto;
    }
    
    public void setDataVoto(LocalDateTime dataVoto) {
        this.dataVoto = dataVoto;
    }
    
    public Professor getProfessor() {
        return professor;
    }
    
    public void setProfessor(Professor professor) {
        this.professor = professor;
    }
    
    public Processo getProcesso() {
        return processo;
    }
    
    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
    
    public Reuniao getReuniao() {
        return reuniao;
    }
    
    public void setReuniao(Reuniao reuniao) {
        this.reuniao = reuniao;
    }
    
    // Métodos de negócio
    
    public boolean isValido() {
        // Voto é válido se NÃO estiver ausente
        return ausente != null && !ausente;
    }
    
    public boolean isFavoravel() {
        // Favorável = votou COM_RELATOR (concordou com o relator)
        return isValido() && tipo == TipoVoto.COM_RELATOR;
    }
    
    public boolean isDivergente() {
        // Divergente = votou DIVERGENTE (discordou do relator)
        return isValido() && tipo == TipoVoto.DIVERGENTE;
    }
    
    public boolean isAusente() {
        return ausente != null && ausente;
    }
    
    
    @Override
    public String toString() {
        if (isAusente()) {
            return "Voto{professor=" + (professor != null ? professor.getNome() : "null") + ", AUSENTE}";
        }
        return "Voto{" +
               "professor=" + (professor != null ? professor.getNome() : "null") +
               ", tipo=" + tipo +
               ", processo=" + (processo != null ? processo.getNumero() : "null") +
               '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Voto voto = (Voto) o;
        return id != null && id.equals(voto.id);
    }
    
}