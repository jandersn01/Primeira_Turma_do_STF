package br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.*;


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
    
    @NotNull(message = "Data de início é obrigatória")
    @PastOrPresent(message = "Data de início não pode ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;
    
    @NotBlank(message = "Portaria é obrigatória")
    @Size(min = 5, max = 50, message = "Portaria deve ter entre 5 e 50 caracteres")
    @Pattern(regexp = "^[Pp]ortaria\\s+\\d+/\\d{4}$", message = "Portaria deve estar no formato: Portaria XXX/YYYY")
    private String portaria;
    
    @NotBlank(message = "Nome do curso é obrigatório")
    @Size(min = 3, max = 100, message = "Nome do curso deve ter entre 3 e 100 caracteres")
    private String curso;
    
    private List<Long> membrosIds = new ArrayList<>();

    


}
