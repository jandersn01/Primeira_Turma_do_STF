package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ReuniaoService;

@Controller
@RequestMapping("/reunioes")
public class ReuniaoController {

    @Autowired
    ReuniaoService reuniaoService;

    @GetMapping("/form")
    public String reuniaoForm(Model model) {
        Long idColegiado = 1L; // Simulação
        model.addAttribute("reuniao", new Reuniao());
        List<Processo> processosDisponiveis = reuniaoService.listarProcessosDisponiveisParaReuniao(idColegiado);
        model.addAttribute("processosDisponiveis", processosDisponiveis);
        return "reuniao/form";
    }

    @GetMapping
    public String listarReunioes(Model model, @RequestParam(name = "status", required = false) StatusReuniao status) {
        Long idColegiado = 1L; // Simulação

        // Se o status vier vazio da URL, tratamos como null para o Service buscar todas
        List<Reuniao> reunioes = reuniaoService.listarReunioesDoColegiado(idColegiado, status);
        
        model.addAttribute("reunioes", reunioes);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("colegiadoId", idColegiado); // Importante para o input hidden no HTML
        
        return "reuniao/list";
    }

    @PostMapping("/save")
    public String criarReuniao(Reuniao reuniao, 
                               @RequestParam(name = "processosIds", required = false) List<Long> processosIds,
                               RedirectAttributes redirectAttributes) {
        try {
            Long idColegiado = 1L; // Simulação
            reuniaoService.criarReuniao(reuniao, processosIds, idColegiado);
            redirectAttributes.addFlashAttribute("mensagem", "Reunião agendada com sucesso!");
            return "redirect:/reunioes"; 
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar reunião: " + e.getMessage());
            return "redirect:/reunioes/form";
        }
    }
}