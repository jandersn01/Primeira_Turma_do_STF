package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AssuntoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProfessorService professorService;

    @Autowired
    private AssuntoService assuntoService;

    @Autowired
    private ColegiadoService colegiadoService;

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String exibirHome(Model model) {
        // Estatísticas gerais de processos
        List<br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo> todosProcessos = processoService.findAll();
        
        long totalProcessos = todosProcessos.size();
        long processosCriados = todosProcessos.stream()
            .filter(p -> p.getStatus() == StatusProcesso.CRIADO)
            .count();
        long processosEmTramitacao = todosProcessos.stream()
            .filter(p -> p.getStatus() == StatusProcesso.DISTRIBUIDO 
                      || p.getStatus() == StatusProcesso.EM_PAUTA 
                      || p.getStatus() == StatusProcesso.EM_JULGAMENTO)
            .count();
        long processosFinalizados = todosProcessos.stream()
            .filter(p -> p.getStatus() == StatusProcesso.JULGADO)
            .count();

        // Estatísticas de cadastros
        long totalAlunos = alunoService.findAll().size();
        long totalProfessores = professorService.findAll().size();
        long totalAssuntos = assuntoService.findAll().size();
        long totalColegiados = colegiadoService.findAll().size();

        // Processos recentes (últimos 5, ordenados por data de recepção)
        List<br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo> processosRecentes = todosProcessos.stream()
            .sorted((p1, p2) -> {
                if (p1.getDataRecepcao() == null && p2.getDataRecepcao() == null) return 0;
                if (p1.getDataRecepcao() == null) return 1;
                if (p2.getDataRecepcao() == null) return -1;
                return p2.getDataRecepcao().compareTo(p1.getDataRecepcao());
            })
            .limit(5)
            .collect(Collectors.toList());

        // Adicionar ao model
        model.addAttribute("totalProcessos", totalProcessos);
        model.addAttribute("processosCriados", processosCriados);
        model.addAttribute("processosEmTramitacao", processosEmTramitacao);
        model.addAttribute("processosFinalizados", processosFinalizados);
        model.addAttribute("totalAlunos", totalAlunos);
        model.addAttribute("totalProfessores", totalProfessores);
        model.addAttribute("totalAssuntos", totalAssuntos);
        model.addAttribute("totalColegiados", totalColegiados);
        model.addAttribute("processosRecentes", processosRecentes);

        return "home";
    }
}
