package br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Curso;
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
        this.portaria = portaria.trim();
    }
    public Long getCursoId() {
        return cursoId;
    }
    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }
    public List<Long> getMembrosIds() {
        return membrosIds;
    }
    public void setMembrosIds(List<Long> membrosIds) {
        this.membrosIds = membrosIds;
    }
    public Long getCoordenadorId() { return coordenadorId; }
    public void setCoordenadorId(Long coordenadorId) { this.coordenadorId = coordenadorId; }
    
    public Long getAlunoId() { return alunoId; }
    public void setAlunoId(Long alunoId) { this.alunoId = alunoId; }
    


    private Long id;
    
    @NotNull(message = "Data de início é obrigatória")
    @PastOrPresent(message = "Data de início não pode ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicio;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFim;
    
    @NotBlank(message = "Portaria é obrigatória")
    @Size(min = 5, max = 50, message = "Portaria deve ter entre 5 e 50 caracteres")
    @Pattern(regexp = "^\\d{1,5}/\\d{4}$", message = "Portaria deve estar no formato: NNN/YYYY")
    private String portaria;
    
    @NotNull(message = "Definir o curso é obrigatório")
    private Long cursoId;

    @NotNull(message = "É obrigatório selecionar um Coordenador")
    private Long coordenadorId; 

    @NotNull(message = "É obrigatório selecionar um Aluno Representante")
    private Long alunoId; 
    
    private List<Long> membrosIds = new ArrayList<>();

    public ColegiadoDTO() {
        this.membrosIds.add(null);
        this.membrosIds.add(null);
        this.membrosIds.add(null);
    }


}
