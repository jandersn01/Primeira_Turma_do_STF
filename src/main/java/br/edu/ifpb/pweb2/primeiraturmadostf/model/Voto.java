package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;

@Entity
public class Voto {
    @Id @GeneratedValue
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private TipoVoto voto;
    
    private Boolean ausente;
    
    @ManyToOne 
    private Professor professor; // Quem votou
    
    @ManyToOne
    private Processo processo;
    
    @ManyToOne 
    private Reuniao reuniao; // Em qual reunião votou
}
