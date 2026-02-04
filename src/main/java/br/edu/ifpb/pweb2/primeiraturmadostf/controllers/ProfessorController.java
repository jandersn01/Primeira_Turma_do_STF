package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.TipoDecisao;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ReuniaoService;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private ColegiadoService colegiadoService;

    @Autowired
    private ReuniaoService reuniaoService;

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

    /**
     * Exibe os detalhes de um processo designado ao relator.
     * Permite que o relator veja as informações e registre seu parecer.
     */
    @GetMapping("/processo/{id}")
    public String verDetalhesProcesso(
            @PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Professor professor = professorService.findByMatricula(userDetails.getUsername());
        if (professor == null) {
            redirectAttributes.addFlashAttribute("erro", "Professor não encontrado.");
            return "redirect:/professor/processo/list";
        }

        Processo processo = processoService.findById(id);
        if (processo == null) {
            redirectAttributes.addFlashAttribute("erro", "Processo não encontrado.");
            return "redirect:/professor/processo/list";
        }

        // Verifica se o professor é o relator do processo (ou admin)
        boolean isAdmin = isAdmin(userDetails);
        if (!isAdmin && (processo.getRelator() == null || !processo.getRelator().getId().equals(professor.getId()))) {
            redirectAttributes.addFlashAttribute("erro", "Você não é o relator deste processo.");
            return "redirect:/professor/processo/list";
        }

        model.addAttribute("processo", processo);
        model.addAttribute("professor", professor);
        model.addAttribute("tiposDecisao", TipoDecisao.values());

        // Verifica se pode dar parecer (apenas se DISTRIBUIDO e não tem parecer ainda)
        boolean podeEmitirParecer = processo.getStatus() == StatusProcesso.DISTRIBUIDO
                && processo.getDecisaoRelator() == null;
        model.addAttribute("podeEmitirParecer", podeEmitirParecer);

        return "professor/processo/detalhes";
    }

    /**
     * Registra o parecer do relator em um processo.
     */
    @PostMapping("/processo/{id}/parecer")
    public String registrarParecer(
            @PathVariable Long id,
            @RequestParam String decisao,
            @RequestParam String parecer,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Professor professor = professorService.findByMatricula(userDetails.getUsername());
            if (professor == null) {
                redirectAttributes.addFlashAttribute("erro", "Professor não encontrado.");
                return "redirect:/professor/processo/list";
            }

            TipoDecisao tipoDecisao = TipoDecisao.valueOf(decisao);
            processoService.registrarParecer(id, professor.getId(), tipoDecisao, parecer);

            redirectAttributes.addFlashAttribute("mensagem", "Parecer registrado com sucesso! O processo está disponível para ser incluído em pauta.");

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/professor/processo/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar parecer: " + e.getMessage());
            return "redirect:/professor/processo/" + id;
        }

        return "redirect:/professor/processo/" + id;
    }

}
