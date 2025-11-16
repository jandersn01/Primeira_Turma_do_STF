package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "processo",
    indexes = {
        @Index(name = "idx_processo_numero", columnList = "numero"),
        @Index(name = "idx_processo_status", columnList = "status"),
        @Index(name = "idx_processo_interessado", columnList = "interessado_id"),
        @Index(name = "idx_processo_relator", columnList = "relator_id"),
        @Index(name = "idx_processo_data_recepcao", columnList = "data_recepcao")
    }
)
public class Processo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 30)
    private String numero; // Ex: "36237464/22" ou "2024/123"
    
    @Column(name = "data_recepcao", nullable = false)
    private LocalDate dataRecepcao;
    
    @Column(name = "data_distribuicao")
    private LocalDate dataDistribuicao;
    
    @Column(name = "data_parecer")
    private LocalDate dataParecer;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String parecer; // Voto fundamentado do relator
    
    // ADICIONADO: Faltava no diagrama mas está no diagrama de estados
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProcesso status = StatusProcesso.CRIADO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "decisao_relator", length = 20)
    private TipoDecisao decisaoRelator;
    
    // Relacionamentos
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assunto_id", nullable = false)
    private Assunto assunto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relator_id")
    private Professor relator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interessado_id", nullable = false)
    private Aluno interessado;
    
    // como não tive a chance de perguntar ao professor, assumi que o processo pode estar em várias pautas
    @ManyToMany(mappedBy = "processos")
    private Set<Reuniao> reunioes = new HashSet<>();
    
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Voto> votos = new HashSet<>();
    

    public Processo() {}
    
    public Processo(String numero, Assunto assunto, Aluno interessado) {
        this.numero = numero;
        this.assunto = assunto;
        this.interessado = interessado;
        this.dataRecepcao = LocalDate.now();
        this.status = StatusProcesso.CRIADO;
    }
    
    // Getters e Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public void setNumero(String numero) {
        this.numero = numero;
    }
    
    public LocalDate getDataRecepcao() {
        return dataRecepcao;
    }
    
    public void setDataRecepcao(LocalDate dataRecepcao) {
        this.dataRecepcao = dataRecepcao;
    }
    
    public LocalDate getDataDistribuicao() {
        return dataDistribuicao;
    }
    
    public void setDataDistribuicao(LocalDate dataDistribuicao) {
        this.dataDistribuicao = dataDistribuicao;
    }
    
    public LocalDate getDataParecer() {
        return dataParecer;
    }
    
    public void setDataParecer(LocalDate dataParecer) {
        this.dataParecer = dataParecer;
    }
    
    public String getParecer() {
        return parecer;
    }
    
    public void setParecer(String parecer) {
        this.parecer = parecer;
    }
    
    public StatusProcesso getStatus() {
        return status;
    }
    
    public void setStatus(StatusProcesso status) {
        this.status = status;
    }
    
    public TipoDecisao getDecisaoRelator() {
        return decisaoRelator;
    }
    
    public void setDecisaoRelator(TipoDecisao decisaoRelator) {
        this.decisaoRelator = decisaoRelator;
    }
    
    public Assunto getAssunto() {
        return assunto;
    }
    
    public void setAssunto(Assunto assunto) {
        this.assunto = assunto;
    }
    
    public Professor getRelator() {
        return relator;
    }
    
    public void setRelator(Professor relator) {
        this.relator = relator;
    }
    
    public Aluno getInteressado() {
        return interessado;
    }
    
    public void setInteressado(Aluno interessado) {
        this.interessado = interessado;
    }
    
    public Set<Reuniao> getReunioes() {
        return reunioes;
    }
    
    public void setReunioes(Set<Reuniao> reunioes) {
        this.reunioes = reunioes;
    }
    
    public Set<Voto> getVotos() {
        return votos;
    }
    
    public void setVotos(Set<Voto> votos) {
        this.votos = votos;
    }
    
    
    public boolean isJulgado() {
        return status == StatusProcesso.JULGADO;
    }
    
    public boolean temRelator() {
        return relator != null;
    }
    
    
    @Override
    public String toString() {
        return "Processo{" +
               "numero='" + numero + '\'' +
               ", status=" + status +
               ", interessado=" + (interessado != null ? interessado.getNome() : "null") +
               ", relator=" + (relator != null ? relator.getNome() : "null") +
               '}';
    }
    
}
