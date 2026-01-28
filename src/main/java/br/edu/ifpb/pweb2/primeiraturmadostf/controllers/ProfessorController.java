package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
            @AuthenticationPrincipal UserDetails userDetails) {

        Professor professor = professorService.findByMatricula(userDetails.getUsername());

        List<Processo> processos = Collections.emptyList();
        if (professor != null) {
            processos = processoService.findByRelator(professor);
        }

        model.addAttribute("processos", processos);
        model.addAttribute("professor", professor);

        if (professor == null) {
            model.addAttribute("mensagem", "Professor nao encontrado.");
        }

        return "professor/processo/list";
    }
}