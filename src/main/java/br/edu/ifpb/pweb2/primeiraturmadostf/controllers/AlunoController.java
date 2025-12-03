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
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Documento;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AssuntoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.DocumentoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        
        // Se processo for null, criar um novo
        if (processo == null) {
            processo = new Processo();
        }
        
        model.addAttribute("processo", processo);
        model.addAttribute("assuntos", assuntoService.findAll());
        model.addAttribute("aluno", aluno);
        
        return "aluno/processo/form";
    }

    @PostMapping("/processo/save")
    public String postProcesso(
            @Valid @ModelAttribute("processo") Processo processo,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        
        // TODO: Quando implementar autenticação, pegar o aluno logado
        // Por enquanto, vamos usar o primeiro aluno como exemplo
        Aluno aluno = alunoService.findAll().stream()
                .findFirst()
                .orElse(null);
        
        if (aluno == null) {
            redirect.addFlashAttribute("mensagem", "Erro: Nenhum aluno cadastrado no sistema.");
            return "redirect:/admin/aluno/form";
        }
        
        // Validação customizada: assunto válido
        if (processo.getAssunto() == null || processo.getAssunto().getId() == null) {
            result.rejectValue("assunto", "assunto.required", "Selecione um assunto válido");
        } else {
        // Carregar o assunto completo do banco
        Assunto assunto = assuntoService.findById(processo.getAssunto().getId());
        if (assunto == null) {
                result.rejectValue("assunto", "assunto.notfound", "Assunto não encontrado");
            } else {
                processo.setAssunto(assunto);
        }
        }
        
        if (result.hasErrors()) {
            // Preparar dados necessários para o formulário
            model.addAttribute("assuntos", assuntoService.findAll());
            model.addAttribute("aluno", aluno);
            return "aluno/processo/form";
        }
        
        // Configurar o processo
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

    @GetMapping("/processo/list")
    @Transactional(readOnly = true)
    public String listarProcessos(
            Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assuntoId,
            @RequestParam(required = false, defaultValue = "desc") String ordenacao) {
        
        // TODO: Quando implementar autenticação, pegar o aluno logado
        // Por enquanto, vamos usar o primeiro aluno como exemplo
        Aluno aluno = alunoService.findAll().stream()
                .findFirst()
                .orElse(null);
        
        // Se não houver aluno, ainda mostramos a página mas com lista vazia
        java.util.List<Processo> processos = new java.util.ArrayList<>();
        
        if (aluno != null) {
            // Converter string de status para enum (se fornecido)
            StatusProcesso statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = StatusProcesso.valueOf(status);
                } catch (IllegalArgumentException e) {
                    // Status inválido, ignorar
                }
            }
            
            // Carregar assunto se fornecido
            Assunto assunto = null;
            if (assuntoId != null) {
                assunto = assuntoService.findById(assuntoId);
            }
            
            // Buscar processos com filtros apenas se houver aluno
            processos = processoService.findByInteressadoWithFilters(
                aluno, statusEnum, assunto, ordenacao);
            
            // Carregar documentos para cada processo (forçar inicialização do lazy)
            for (Processo p : processos) {
                try {
                    java.util.Set<Documento> documentos = new java.util.HashSet<>(documentoService.findByProcesso(p));
                    p.setDocumentos(documentos);
                } catch (Exception e) {
                    // Se houver erro ao carregar documentos, inicializa com lista vazia
                    p.setDocumentos(new java.util.HashSet<>());
                }
                // Forçar inicialização dos relacionamentos lazy para evitar LazyInitializationException
                try {
                    if (p.getAssunto() != null) {
                        p.getAssunto().getNome(); // Inicializa o assunto
                    }
                    if (p.getInteressado() != null) {
                        p.getInteressado().getNome(); // Inicializa o interessado
                    }
                } catch (Exception e) {
                    // Ignorar erros de inicialização lazy
                }
            }
        }
        
        // Adicionar atributos ao model
        model.addAttribute("processos", processos);
        model.addAttribute("aluno", aluno);
        model.addAttribute("assuntos", assuntoService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());
        
        // Manter valores dos filtros selecionados
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("assuntoSelecionado", assuntoId);
        model.addAttribute("ordenacaoSelecionada", ordenacao);
        
        // Mensagem informativa se não houver aluno
        if (aluno == null) {
            model.addAttribute("mensagem", "Nenhum aluno cadastrado no sistema. Por favor, cadastre um aluno primeiro para visualizar seus processos.");
        }
        
        return "aluno/processo/list";
    }
    
    @PostMapping("/processo/{processoId}/documento/upload")
    public String uploadDocumento(
            @PathVariable Long processoId,
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam(required = false) String descricao,
            RedirectAttributes redirect) {
        
        try {
            // Buscar processo
            Processo processo = processoService.findById(processoId);
            if (processo == null) {
                redirect.addFlashAttribute("mensagem", "Erro: Processo não encontrado.");
                return "redirect:/aluno/processo/list";
            }
            
            // Verificar se o processo pertence ao aluno logado
            // TODO: Quando implementar autenticação, verificar se o processo pertence ao aluno logado
            Aluno aluno = alunoService.findAll().stream()
                    .findFirst()
                    .orElse(null);
            
            if (aluno == null || !processo.getInteressado().getId().equals(aluno.getId())) {
                redirect.addFlashAttribute("mensagem", "Erro: Você não tem permissão para adicionar documentos a este processo.");
                return "redirect:/aluno/processo/list";
            }
            
            // Fazer upload do documento
            documentoService.uploadDocumento(arquivo, processo, descricao);
            
            redirect.addFlashAttribute("mensagem", "Documento enviado com sucesso!");
            
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("mensagem", "Erro: " + e.getMessage());
        } catch (IOException e) {
            redirect.addFlashAttribute("mensagem", "Erro ao salvar o arquivo. Tente novamente.");
        }
        
        return "redirect:/aluno/processo/list";
    }
    
    @GetMapping("/processo/{processoId}/documento/{documentoId}/download")
    public ResponseEntity<Resource> downloadDocumento(
            @PathVariable Long processoId,
            @PathVariable Long documentoId) {
        
        try {
            // Buscar documento
            Documento documento = documentoService.findById(documentoId);
            if (documento == null || !documento.getProcesso().getId().equals(processoId)) {
                return ResponseEntity.notFound().build();
            }
            
            // Verificar se o processo pertence ao aluno logado
            // TODO: Quando implementar autenticação, verificar se o processo pertence ao aluno logado
            
            // Carregar arquivo
            Path caminhoArquivo = Paths.get(documento.getCaminho());
            if (!Files.exists(caminhoArquivo)) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new FileSystemResource(caminhoArquivo);
            String contentType = Files.probeContentType(caminhoArquivo);
            if (contentType == null) {
                contentType = documento.getTipoMime();
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + documento.getNomeOriginal() + "\"")
                    .body(resource);
                    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/processo/{processoId}/documento/{documentoId}/delete")
    public String deletarDocumento(
            @PathVariable Long processoId,
            @PathVariable Long documentoId,
            RedirectAttributes redirect) {
        
        try {
            // Buscar documento
            Documento documento = documentoService.findById(documentoId);
            if (documento == null || !documento.getProcesso().getId().equals(processoId)) {
                redirect.addFlashAttribute("mensagem", "Erro: Documento não encontrado.");
                return "redirect:/aluno/processo/list";
            }
            
            // Verificar se o processo pertence ao aluno logado
            // TODO: Quando implementar autenticação, verificar se o processo pertence ao aluno logado
            Aluno aluno = alunoService.findAll().stream()
                    .findFirst()
                    .orElse(null);
            
            if (aluno == null || !documento.getProcesso().getInteressado().getId().equals(aluno.getId())) {
                redirect.addFlashAttribute("mensagem", "Erro: Você não tem permissão para remover este documento.");
                return "redirect:/aluno/processo/list";
            }
            
            // Remover documento
            documentoService.removerDocumento(documentoId);
            redirect.addFlashAttribute("mensagem", "Documento removido com sucesso!");
            
        } catch (IOException e) {
            redirect.addFlashAttribute("mensagem", "Erro ao remover o documento. Tente novamente.");
        }
        
        return "redirect:/aluno/processo/list";
    }

}


