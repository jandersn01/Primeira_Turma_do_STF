package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private ProfessorService professorService;

    private boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    @GetMapping("/processo/list")
    public String listarProcessosDesignados(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long professorId) {

        boolean admin = isAdmin(userDetails);
        Professor professor = null;

        if (admin) {
            // Admin pode filtrar por professor ou ver todos
            if (professorId != null) {
                professor = professorService.findById(professorId);
            }
            model.addAttribute("todosProfessores", professorService.findAll());
            model.addAttribute("isAdmin", true);
        } else {
            professor = professorService.findByMatricula(userDetails.getUsername());
            model.addAttribute("isAdmin", false);
        }

        List<Processo> processos = new ArrayList<>();

        if (admin && professor == null) {
            // Admin sem filtro: mostrar todos os processos que tem relator
            processos = processoService.findAll().stream()
                    .filter(p -> p.getRelator() != null)
                    .toList();
        } else if (professor != null) {
            processos = processoService.findByRelator(professor);
        }

        model.addAttribute("processos", processos);
        model.addAttribute("professor", professor);
        model.addAttribute("professorIdSelecionado", professorId);

        if (professor == null && !admin) {
            model.addAttribute("mensagem", "Professor nao encontrado.");
        }

        return "professor/processo/list";
    }
}