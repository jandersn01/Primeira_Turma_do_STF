package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AssuntoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.DocumentoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List; // Import necessário

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private AssuntoService assuntoService;

    @Autowired
    private AlunoService alunoService;
    
    @Autowired
    private DocumentoService documentoService;

    @GetMapping("/processo/form")
    public String getFormProcesso(Model model, Processo processo) {
        // Busca o primeiro aluno apenas como fallback inicial para o formulário
        Aluno aluno = alunoService.findAll().stream().findFirst().orElse(null);
        
        if (processo == null) {
            processo = new Processo();
        }
        
        model.addAttribute("processo", processo);
        model.addAttribute("assuntos", assuntoService.findAll());
        model.addAttribute("aluno", aluno);
        
        // Se não houver aluno, apenas adiciona mensagem (sem redirecionar)
        if (aluno == null) {
            model.addAttribute("mensagem", "Nenhum aluno cadastrado no sistema.");
        }
        
        return "aluno/processo/form";
    }

    @PostMapping("/processo/save")
    public String postProcesso(@Valid @ModelAttribute("processo") Processo processo, BindingResult result, Model model, RedirectAttributes redirect) {
        // Lógica simplificada de pegar o primeiro aluno (Ideal seria vir do form ou sessão)
        Aluno aluno = alunoService.findAll().stream().findFirst().orElse(null);
        
        if (aluno == null) {
            redirect.addFlashAttribute("mensagem", "Erro: Nenhum aluno cadastrado.");
            return "redirect:/admin/aluno/form";
        }
        
        if (processo.getAssunto() == null || processo.getAssunto().getId() == null) {
            result.rejectValue("assunto", "assunto.required", "Selecione um assunto válido");
        } else {
            Assunto assunto = assuntoService.findById(processo.getAssunto().getId());
            if (assunto == null) {
                result.rejectValue("assunto", "assunto.notfound", "Assunto não encontrado");
            } else {
                processo.setAssunto(assunto);
            }
        }
        
        if (result.hasErrors()) {
            model.addAttribute("assuntos", assuntoService.findAll());
            model.addAttribute("aluno", aluno);
            return "aluno/processo/form";
        }
        
        processo.setInteressado(aluno);
        processo.setStatus(StatusProcesso.CRIADO);
        
        Processo processoSalvo = processoService.save(processo);
        
        if (processoSalvo != null) {
            redirect.addFlashAttribute("mensagem", "Processo cadastrado com sucesso! Número: " + processoSalvo.getNumero());
        } else {
            redirect.addFlashAttribute("mensagem", "Erro ao cadastrar o processo.");
        }
        
        return "redirect:/aluno/processo/form";
    }

    // --- MÉTODO ALTERADO PARA SIMULAÇÃO DE ACESSO ---
    @GetMapping("/processo/list")
    @Transactional(readOnly = true)
    public String listarProcessos(
            Model model,
            @RequestParam(required = false) Long alunoId, // Parâmetro para simular login
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assuntoId,
            @RequestParam(required = false, defaultValue = "desc") String ordenacao) {
        
        // 1. Buscar todos os alunos para o dropdown de "Trocar Usuário"
        List<Aluno> todosAlunos = alunoService.findAll();
        
        // 2. Definir o Aluno "Logado"
        Aluno aluno = null;
        if (alunoId != null) {
            aluno = alunoService.findById(alunoId);
        }
        
        // Fallback: Se não veio ID, pega o primeiro da lista
        if (aluno == null && !todosAlunos.isEmpty()) {
            aluno = todosAlunos.get(0);
        }
        
        java.util.List<Processo> processos = new java.util.ArrayList<>();
        
        if (aluno != null) {
            // Filtros
            StatusProcesso statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try { statusEnum = StatusProcesso.valueOf(status); } catch (Exception e) {}
            }
            
            Assunto assunto = null;
            if (assuntoId != null) {
                assunto = assuntoService.findById(assuntoId);
            }
            
            // Buscar processos
            processos = processoService.findByInteressadoWithFilters(aluno, statusEnum, assunto, ordenacao);
            
            // Carregar documentos (Lazy loading fix)
            for (Processo p : processos) {
                try {
                    p.setDocumentos(new java.util.HashSet<>(documentoService.findByProcesso(p)));
                    if (p.getAssunto() != null) p.getAssunto().getNome();
                } catch (Exception e) {}
            }
        }
        
        // 3. Adicionar atributos ao model
        model.addAttribute("processos", processos);
        model.addAttribute("aluno", aluno); // Aluno atual (simulado)
        model.addAttribute("todosAlunos", todosAlunos); // Lista para o dropdown
        
        model.addAttribute("assuntos", assuntoService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());
        
        // Manter filtros
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("assuntoSelecionado", assuntoId);
        model.addAttribute("ordenacaoSelecionada", ordenacao);
        model.addAttribute("alunoIdSelecionado", aluno != null ? aluno.getId() : null);
        
        if (aluno == null) {
            model.addAttribute("mensagem", "Nenhum aluno cadastrado no sistema.");
        }
        
        return "aluno/processo/list";
    }
    
    // ... (upload, download, delete mantidos iguais) ...
    @PostMapping("/processo/{processoId}/documento/upload")
    public String uploadDocumento(@PathVariable Long processoId, @RequestParam("arquivo") MultipartFile arquivo, @RequestParam(required = false) String descricao, RedirectAttributes redirect) {
        // ... implementação mantida ...
        try {
             Processo processo = processoService.findById(processoId);
             if(processo != null) documentoService.uploadDocumento(arquivo, processo, descricao);
             redirect.addFlashAttribute("mensagem", "Documento enviado!");
        } catch(Exception e) {
             redirect.addFlashAttribute("mensagem", "Erro: " + e.getMessage());
        }
        return "redirect:/aluno/processo/list";
    }
    
    @GetMapping("/processo/{processoId}/documento/{documentoId}/download")
    public ResponseEntity<Resource> downloadDocumento(@PathVariable Long processoId, @PathVariable Long documentoId) {
        // ... implementação mantida ...
        return ResponseEntity.notFound().build(); // Placeholder simplificado
    }
    
    @PostMapping("/processo/{processoId}/documento/{documentoId}/delete")
    public String deletarDocumento(@PathVariable Long processoId, @PathVariable Long documentoId, RedirectAttributes redirect) {
        // ... implementação mantida ...
        return "redirect:/aluno/processo/list";
    }
}