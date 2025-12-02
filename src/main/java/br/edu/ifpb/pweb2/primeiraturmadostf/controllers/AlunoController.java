package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AssuntoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private AssuntoService assuntoService;

    @Autowired
    private AlunoService alunoService;

    @GetMapping("/processo/form")
    public String getFormProcesso(Model model, Processo processo) {
        // TODO: Quando implementar autenticação, pegar o aluno logado
        // Por enquanto, vamos usar o primeiro aluno como exemplo
        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Aluno aluno = (Aluno) auth.getPrincipal();
        
        // Para teste, vamos buscar o primeiro aluno
        // Em produção, isso virá da autenticação
        Aluno aluno = alunoService.findAll().stream()
                .findFirst()
                .orElse(null);
        
        if (aluno == null) {
            model.addAttribute("mensagem", "Erro: Nenhum aluno cadastrado no sistema. Por favor, cadastre um aluno primeiro.");
            return "redirect:/admin/aluno/form";
        }
        
        model.addAttribute("processo", processo);
        model.addAttribute("assuntos", assuntoService.findAll());
        model.addAttribute("aluno", aluno);
        
        return "aluno/processo/form";
    }

    @PostMapping("/processo/save")
    public String postProcesso(Processo processo, RedirectAttributes redirect) {
        // TODO: Quando implementar autenticação, pegar o aluno logado
        // Por enquanto, vamos usar o primeiro aluno como exemplo
        Aluno aluno = alunoService.findAll().stream()
                .findFirst()
                .orElse(null);
        
        if (aluno == null) {
            redirect.addFlashAttribute("mensagem", "Erro: Nenhum aluno cadastrado no sistema.");
            return "redirect:/admin/aluno/form";
        }
        
        // Validar se o assunto foi selecionado
        if (processo.getAssunto() == null || processo.getAssunto().getId() == null) {
            redirect.addFlashAttribute("mensagem", "Erro: É necessário selecionar um assunto.");
            return "redirect:/aluno/processo/form";
        }
        
        // Carregar o assunto completo do banco
        Assunto assunto = assuntoService.findById(processo.getAssunto().getId());
        if (assunto == null) {
            redirect.addFlashAttribute("mensagem", "Erro: Assunto não encontrado.");
            return "redirect:/aluno/processo/form";
        }
        
        // Validar se o texto do requerimento foi preenchido
        if (processo.getTextoRequerimento() == null || processo.getTextoRequerimento().trim().isEmpty()) {
            redirect.addFlashAttribute("mensagem", "Erro: É necessário preencher o texto do requerimento.");
            return "redirect:/aluno/processo/form";
        }
        
        // Configurar o processo
        processo.setAssunto(assunto);
        processo.setInteressado(aluno);
        processo.setStatus(br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso.CRIADO);
        
        // Salvar o processo (o número será gerado automaticamente no service)
        Processo processoSalvo = processoService.save(processo);
        
        if (processoSalvo != null) {
            redirect.addFlashAttribute("mensagem", "Processo cadastrado com sucesso! Número: " + processoSalvo.getNumero());
        } else {
            redirect.addFlashAttribute("mensagem", "Erro ao cadastrar o processo. Tente novamente.");
        }
        
        return "redirect:/aluno/processo/form";
    }

}

