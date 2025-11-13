package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reuniao")
public class Reuniao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "data_reuniao", nullable = false)
    private LocalDate dataReuniao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusReuniao status = StatusReuniao.PROGRAMADA;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String ata;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colegiado_id", nullable = false)
    private Colegiado colegiado;

    @ManyToMany
    @JoinTable(
        name = "reuniao_processo",
        joinColumns = @JoinColumn(name = "reuniao_id"),
        inverseJoinColumns = @JoinColumn(name = "processo_id")
    )
    private Set<Processo> processos = new HashSet<>();
    
    @OneToMany(mappedBy = "reuniao")
    private Set<Voto> votos = new HashSet<>();
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getDataReuniao() {
        return dataReuniao;
    }
    
    public void setDataReuniao(LocalDate dataReuniao) {
        this.dataReuniao = dataReuniao;
    }
    
    public StatusReuniao getStatus() {
        return status;
    }
    
    public void setStatus(StatusReuniao status) {
        this.status = status;
    }
    
    public String getAta() {
        return ata;
    }
    
    public void setAta(String ata) {
        this.ata = ata;
    }
    
    public Colegiado getColegiado() {
        return colegiado;
    }
    
    public void setColegiado(Colegiado colegiado) {
        this.colegiado = colegiado;
    }
    
    public Set<Processo> getProcessos() {
        return processos;
    }
    
    public void setProcessos(Set<Processo> processos) {
        this.processos = processos;
    }

    public Set<Voto> getVotos() {
        return votos;
    }
    
    public void setVotos(Set<Voto> votos) {
        this.votos = votos;
    }
    
    @Override
    public String toString() {
        return "Reuniao{id=" + id + 
               ", data=" + dataReuniao + 
               ", status=" + status + 
               ", processos=" + processos.size() + "}";
    }
}