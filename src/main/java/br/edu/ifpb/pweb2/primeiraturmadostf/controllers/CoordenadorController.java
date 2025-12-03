package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/coordenador")
public class CoordenadorController {

    @Autowired
    private ProcessoService processoService;

    @Autowired
    private ColegiadoService colegiadoService;

    @Autowired
    private AlunoService alunoService;

    @Autowired
    private ProfessorService professorService;

    @GetMapping("/processo/list")
    public String listarProcessosColegiado(
            Model model,
            @RequestParam(required = false) Long colegiadoId,
            @RequestParam(required = false) Long coordenadorId, // ID para simular login!!!!!!!!!!!!!!!!!!!
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long relatorId,
            @RequestParam(required = false, defaultValue = "desc") String ordenacao) {


        List<Professor> todosCoordenadores = professorService.findByCoordenadores();

        if (todosCoordenadores.isEmpty()) {
            model.addAttribute("mensagem", "Nenhum coordenador encontrado no sistema.");
            return "coordenador/processo/list";
        }

        // 2. Definir o Coordenador Ativo ("Logado")
        Professor coordenador = null;
        if (coordenadorId != null) {
            // Tenta achar o coordenador selecionado
            coordenador = professorService.findById(coordenadorId);
        }
        
        // Se não veio ID ou não achou, pega o primeiro como fallback
        if (coordenador == null) {
            coordenador = todosCoordenadores.get(0);
        }

        // 3. Buscar colegiados DESTE coordenador específico
        List<Colegiado> colegiados = new ArrayList<>(coordenador.getColegiados());
        if (colegiados.isEmpty()) {
            // Fallback apenas para não quebrar a tela se o coordenador não tiver colegiado
            // Em produção real, talvez fosse melhor mostrar msg vazia
             colegiados = colegiadoService.findAll(); 
        }

        // 4. Define qual colegiado está selecionado
        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

        // 5. Busca os processos filtrados
        List<Processo> processos = new ArrayList<>();
        if (colegiadoSelecionado != null) {
            StatusProcesso statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try { statusEnum = StatusProcesso.valueOf(status); } catch (Exception e) {}
            }

            Aluno aluno = (alunoId != null) ? alunoService.findById(alunoId) : null;
            Professor relator = (relatorId != null) ? professorService.findById(relatorId) : null;

            processos = processoService.findByColegiadoWithFilters(
                colegiadoSelecionado, statusEnum, aluno, relator, ordenacao);
        }

        // Popula a View
        model.addAttribute("processos", processos);
        
        // Dados para o seletor de troca de usuário
        model.addAttribute("todosCoordenadores", todosCoordenadores);
        model.addAttribute("coordenador", coordenador); // O usuário "logado" atual
        
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);
        
        // Listas para os filtros
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("professores", professorService.findAll()); 
        model.addAttribute("statusList", StatusProcesso.values());

        // Mantém o estado dos filtros
        model.addAttribute("coordenadorIdSelecionado", coordenador.getId()); // ID do logado atual
        model.addAttribute("colegiadoIdSelecionado", colegiadoId);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("alunoIdSelecionado", alunoId);
        model.addAttribute("relatorIdSelecionado", relatorId);
        model.addAttribute("ordenacaoSelecionada", ordenacao);

        return "coordenador/processo/list";
    }

    // Distribuir processo para relator
    @PostMapping("/processo/distribuir")
    public String distribuirProcesso(
            @RequestParam("processoId") Long processoId,
            @RequestParam("relatorId") Long relatorId,
            @RequestParam(value = "colegiadoId", required = false) Long colegiadoId,
            @RequestParam(value = "coordenadorId", required = false) Long coordenadorId, // Recebe para manter login
            RedirectAttributes attr) {
        
        try {
            processoService.distribuirProcesso(processoId, relatorId);
            attr.addFlashAttribute("mensagem", "Processo distribuído com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("mensagemErro", "Erro ao distribuir: " + e.getMessage());
        }
        
        // Monta o redirect mantendo o usuário logado
        StringBuilder redirect = new StringBuilder("redirect:/coordenador/processo/list?");
        if (colegiadoId != null) redirect.append("colegiadoId=").append(colegiadoId).append("&");
        if (coordenadorId != null) redirect.append("coordenadorId=").append(coordenadorId);
        
        return redirect.toString();
    }
}