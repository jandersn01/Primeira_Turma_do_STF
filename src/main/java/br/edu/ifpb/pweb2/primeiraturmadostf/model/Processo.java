package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    
    @NotBlank(message = "Texto do requerimento é obrigatório")
    @Size(max = 5000, message = "Texto do requerimento deve ter no máximo 5000 caracteres")
    @Lob
    @Column(name = "texto_requerimento", columnDefinition = "TEXT", nullable = false)
    private String textoRequerimento; // Texto do requerimento do aluno
    
    // ADICIONADO: Faltava no diagrama mas está no diagrama de estados
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProcesso status = StatusProcesso.CRIADO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "decisao_relator", length = 20)
    private TipoDecisao decisaoRelator;
    
    // Relacionamentos
    
    @NotNull(message = "Assunto é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assunto_id", nullable = false)
    private Assunto assunto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relator_id")
    private Professor relator;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interessado_id", nullable = false)
    private Aluno interessado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colegiado_id")
    private Colegiado colegiado;
    
    // como não tive a chance de perguntar ao professor, assumi que o processo pode estar em várias pautas
    @ManyToMany(mappedBy = "processos")
    private Set<Reuniao> reunioes = new HashSet<>();
    
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Voto> votos = new HashSet<>();
    
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Documento> documentos = new HashSet<>();

    public Processo() {}
    
    public Processo(String numero, Assunto assunto, Aluno interessado) {
        this.numero = numero;
        this.assunto = assunto;
        this.interessado = interessado;
        this.colegiado = colegiado;
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

    public Colegiado getColegiado() { return colegiado; }
    public void setColegiado(Colegiado colegiado) { this.colegiado = colegiado; }
    
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
    
    public String getTextoRequerimento() {
        return textoRequerimento;
    }
    
    public void setTextoRequerimento(String textoRequerimento) {
        this.textoRequerimento = textoRequerimento;
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
    
    public Set<Documento> getDocumentos() {
        return documentos;
    }
    
    public void setDocumentos(Set<Documento> documentos) {
        this.documentos = documentos;
    }
    
    public void addDocumento(Documento documento) {
        documentos.add(documento);
        documento.setProcesso(this);
    }
    
    public void removeDocumento(Documento documento) {
        documentos.remove(documento);
        documento.setProcesso(null);
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
