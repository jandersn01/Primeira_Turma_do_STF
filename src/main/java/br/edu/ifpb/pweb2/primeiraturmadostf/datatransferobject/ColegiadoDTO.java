package br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;


public class ColegiadoDTO {
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDate getDataInicio() {
        return dataInicio;
    }
    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
    public LocalDate getDataFim() {
        return dataFim;
    }
    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }
    public String getPortaria() {
        return portaria;
    }
    public void setPortaria(String portaria) {
        this.portaria = portaria;
    }
    public String getCurso() {
        return curso;
    }
    public void setCurso(String curso) {
        this.curso = curso;
    }
    public List<Long> getMembrosIds() {
        return membrosIds;
    }
    public void setMembrosIds(List<Long> membrosIds) {
        this.membrosIds = membrosIds;
    }
    private Long id;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;
    private String portaria;
    private String curso;
    private List<Long> membrosIds = new ArrayList<>();

    


}
