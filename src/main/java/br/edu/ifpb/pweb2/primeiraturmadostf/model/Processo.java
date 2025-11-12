package br.edu.ifpb.pweb2.primeiraturmadostf.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
public class Processo {

    @Id @GeneratedValue
    private Long id;

    private String numero;
    private LocalDate dataRecepcao;
    private LocalDate dataDistribuicao;
    private LocalDate dataParecer;

    @Lob
    private String parecer;

    @Enumerated(EnumType.STRING)
    private StatusProcesso status;

    @Enumerated(EnumType.STRING)
    private TipoDecisao decisaoRelator;

    @ManyToOne
    private Assunto assunto;
    
    @ManyToOne
    private Professor relator;
    
    @ManyToOne
    private Aluno interessado;

    @ManyToMany(mappedBy = "processos")
    private Set<Reuniao> reunioes; 
    
    @OneToMany(mappedBy = "processo")
    private Set<Voto> votos;

}
