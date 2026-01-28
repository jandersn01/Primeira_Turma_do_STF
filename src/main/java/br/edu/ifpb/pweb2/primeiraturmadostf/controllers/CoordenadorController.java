package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ReuniaoService;

@Controller
@RequestMapping("/coordenador")
public class CoordenadorController {

    @Autowired
    private ReuniaoService reuniaoService;

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
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long colegiadoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long relatorId,
            @RequestParam(required = false, defaultValue = "desc") String ordenacao) {

        // 1. Verifica se é ADMIN baseado na autoridade correta (ROLE_ADMIN)
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // 2. Busca o professor (pode ser null se for o admin do sistema)
        Professor coordenador = professorService.findByMatricula(userDetails.getUsername());

        // 3. Validação de Acesso: Admin passa direto. Outros precisam ser coordenadores.
        if (!isAdmin && (coordenador == null || !coordenador.getCoordenador())) {
            model.addAttribute("mensagem", "Você não possui permissão de Coordenador.");
            return "coordenador/processo/list";
        }

        // 4. Define quais colegiados mostrar no filtro
        List<Colegiado> colegiados;
        if (isAdmin) {
            colegiados = colegiadoService.findAll(); // Admin vê todos os cursos
        } else {
            colegiados = new ArrayList<>(coordenador.getColegiados());
            if (colegiados.isEmpty()) {
                colegiados = colegiadoService.findAll();
            }
        }

        // 5. Gerencia o colegiado selecionado
        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

        // 6. Busca os processos filtrados
        List<Processo> processos = new ArrayList<>();
        if (colegiadoSelecionado != null) {
            StatusProcesso statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = StatusProcesso.valueOf(status);
                } catch (Exception e) {
                }
            }
            Aluno aluno = (alunoId != null) ? alunoService.findById(alunoId) : null;
            Professor relator = (relatorId != null) ? professorService.findById(relatorId) : null;
            processos = processoService.findByColegiadoWithFilters(
                    colegiadoSelecionado, statusEnum, aluno, relator, ordenacao);
        }

        // 7. Adiciona atributos ao Model
        model.addAttribute("processos", processos);
        model.addAttribute("coordenador", coordenador);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);
        model.addAttribute("isAdmin", isAdmin);

        // Listas para os filtros
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());

        // Mantém os filtros selecionados na UI
        model.addAttribute("colegiadoIdSelecionado", colegiadoId);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("alunoIdSelecionado", alunoId);
        model.addAttribute("relatorIdSelecionado", relatorId);
        model.addAttribute("ordenacaoSelecionada", ordenacao);

        return "coordenador/processo/list";
    }

    @PostMapping("/processo/distribuir")
    public String distribuirProcesso(
            @RequestParam("processoId") Long processoId,
            @RequestParam("relatorId") Long relatorId,
            @RequestParam(value = "colegiadoId", required = false) Long colegiadoId,
            RedirectAttributes attr) {

        try {
            processoService.distribuirProcesso(processoId, relatorId);
            attr.addFlashAttribute("mensagem", "Processo distribuido com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("mensagemErro", "Erro ao distribuir: " + e.getMessage());
        }

        if (colegiadoId != null) {
            return "redirect:/coordenador/processo/list?colegiadoId=" + colegiadoId;
        }
        return "redirect:/coordenador/processo/list";
    }

}
