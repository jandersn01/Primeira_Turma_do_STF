package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/processo/list")
    public String listarProcessosDesignados(
            Model model,
            @RequestParam(required = false) Long professorId) { // Parâmetro opcional para "Login"

        List<Professor> todosProfessores = professorService.findAll();
        
        Professor professor = null;
        
        if (professorId != null) {
            professor = professorService.findById(professorId);
        }
        
        if (professor == null && !todosProfessores.isEmpty()) {
            professor = todosProfessores.get(0);
        }
        
        List<Processo> processos = Collections.emptyList();
        if (professor != null) {
            processos = processoService.findByRelator(professor);
        }
        
        model.addAttribute("processos", processos);
        model.addAttribute("professor", professor); 
        model.addAttribute("todosProfessores", todosProfessores); 
        model.addAttribute("professorIdSelecionado", professor != null ? professor.getId() : null);
        
        return "professor/processo/list";
    }

}