package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ReuniaoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/reunioes")
public class ReuniaoController {

    @Autowired
    ReuniaoService reuniaoService;
    
    @Autowired
    ColegiadoService colegiadoService;
    
    @Autowired
    ProfessorService professorService;

    @GetMapping("/form")
    public String reuniaoForm(Model model) {
        Long idColegiado = 1L; // Simulação
        model.addAttribute("reuniao", new Reuniao());
        List<Processo> processosDisponiveis = reuniaoService.listarProcessosDisponiveisParaReuniao(idColegiado);
        model.addAttribute("processosDisponiveis", processosDisponiveis);
        return "reuniao/form";
    }

    @GetMapping
    public String listarReunioes(
            Model model, 
            @RequestParam(name = "status", required = false) StatusReuniao status,
            @RequestParam(name = "colegiadoId", required = false) Long colegiadoId,
            @RequestParam(name = "professorId", required = false) Long professorId) { // Simulação de Login

        List<Reuniao> reunioes = new ArrayList<>();
        
        // Carrega listas para os filtros
        model.addAttribute("colegiados", colegiadoService.findAll());
        model.addAttribute("professores", professorService.findAll());

        // Lógica de decisão da View (REQ 4 vs REQ 6)
        if (professorId != null) {
            // REQFUNC 6: Professor vê suas reuniões (onde é membro)
            reunioes = reuniaoService.listarReunioesDoProfessor(professorId, status);
            model.addAttribute("professorIdSelecionado", professorId);
            // Busca o objeto professor para exibir o nome na tela
            Professor professor = professorService.findById(professorId);
            model.addAttribute("professorLogado", professor);
        } else if (colegiadoId != null) {
            // REQFUNC 4: Visão Geral de um Colegiado Específico
            reunioes = reuniaoService.listarReunioesDoColegiado(colegiadoId, status);
            model.addAttribute("colegiadoIdSelecionado", colegiadoId);
        } else {
            // Fallback: Se nada selecionado, carrega do colegiado 1 (comportamento antigo) ou lista vazia
            // Vamos manter o comportamento padrão de listar do colegiado 1 para não quebrar fluxo do coordenador
            Long defaultColegiadoId = 1L;
            reunioes = reuniaoService.listarReunioesDoColegiado(defaultColegiadoId, status);
            model.addAttribute("colegiadoIdSelecionado", defaultColegiadoId);
        }

        model.addAttribute("reunioes", reunioes);
        model.addAttribute("statusSelecionado", status);

        return "reuniao/list";
    }

    @PostMapping("/save")
    public String criarReuniao(
            @Valid Reuniao reuniao,
            BindingResult result,
            @RequestParam(name = "processosIds", required = false) List<Long> processosIds,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            Long idColegiado = 1L;
            List<Processo> processosDisponiveis = reuniaoService.listarProcessosDisponiveisParaReuniao(idColegiado);
            model.addAttribute("processosDisponiveis", processosDisponiveis);
            return "reuniao/form";
        }

        try {
            Long idColegiado = 1L;
            reuniaoService.criarReuniao(reuniao, processosIds, idColegiado);
            redirectAttributes.addFlashAttribute("mensagem", "Reunião agendada com sucesso!");
            return "redirect:/reunioes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar: " + e.getMessage());
            return "redirect:/reunioes/form";
        }
    }

    @GetMapping("/{id}/detalhes")
    public String verDetalhes(@PathVariable Long id, Model model) {
        Reuniao reuniao = reuniaoService.findById(id);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reunião não encontrada");
        }
        model.addAttribute("reuniao", reuniao);
        return "reuniao/detalhes";
    }

    @PostMapping("/delete/{id}")
    public String excluirReuniao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean removido = reuniaoService.remove(id);
            if (removido) {
                redirectAttributes.addFlashAttribute("mensagem", "Reunião cancelada com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Não foi possível encontrar a reunião.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir: " + e.getMessage());
        }
        return "redirect:/reunioes";
    }
}