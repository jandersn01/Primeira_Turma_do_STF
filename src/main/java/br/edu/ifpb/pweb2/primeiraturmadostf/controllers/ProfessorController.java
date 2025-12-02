package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/processo/list")
    public String listarProcessosDesignados(Model model) {
        // TODO: Quando implementar autenticação, pegar o professor logado
        // Por enquanto, vamos usar o primeiro professor como exemplo
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Professor professor = (Professor) auth.getPrincipal();
        
        // Para teste, vamos buscar o primeiro professor
        // Em produção, isso virá da autenticação
        Professor professor = professorService.findAll().stream()
                .findFirst()
                .orElse(null);
        
        if (professor != null) {
            model.addAttribute("processos", processoService.findByRelator(professor));
            model.addAttribute("professor", professor);
        } else {
            model.addAttribute("processos", java.util.Collections.emptyList());
        }
        
        return "professor/processo/list";
    }

}

