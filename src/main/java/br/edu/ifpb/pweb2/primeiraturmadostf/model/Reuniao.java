package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;

@Entity
public class Reuniao {
    @Id @GeneratedValue
    private Long id;
    
    private LocalDate dataReuniao;
    
    @Enumerated(EnumType.STRING)
    private StatusReuniao status;
    
    @Lob
    private String ata;
    
    @ManyToMany
    @JoinTable(name = "reuniao_processo") // Pauta
    private Set<Processo> processos;
    
    @ManyToMany
    @JoinTable(name = "reuniao_professor") // Membros presentes
    private Set<Professor> membros;
}