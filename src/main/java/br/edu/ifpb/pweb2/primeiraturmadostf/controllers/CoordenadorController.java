package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
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

    /**
     * Helper para verificar se o usuário logado possui a role ADMIN.
     */
    private boolean checkAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }

    @GetMapping("/processo/list")
    public String listarProcessosColegiado(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long colegiadoId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long relatorId,
            @RequestParam(required = false, defaultValue = "desc") String ordenacao) {

        // 1. Identificação de permissões
        boolean isAdmin = checkAdmin(userDetails);
        Professor coordenador = professorService.findByMatricula(userDetails.getUsername());

        // 2. Validação de Segurança: Acesso permitido apenas se for Admin ou se o Professor for Coordenador
        if (!isAdmin && (coordenador == null || !coordenador.getCoordenador())) {
            model.addAttribute("mensagem", "Acesso restrito a coordenadores.");
            return "coordenador/processo/list";
        }

        // 3. Definição da lista de Colegiados gerenciáveis
        List<Colegiado> colegiados;
        if (isAdmin) {
            colegiados = colegiadoService.findAll(); // Admin vê tudo
        } else {
            // Coordenador vê os colegiados aos quais pertence
            colegiados = new ArrayList<>(coordenador.getColegiados());
            // Fallback caso a lista esteja vazia para não travar a tela
            if (colegiados.isEmpty()) {
                colegiados = colegiadoService.findAll();
            }
        }

        // 4. Seleção do Colegiado para filtragem
        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

        // 5. Busca dos Processos com base nos filtros dinâmicos
        List<Processo> processos = new ArrayList<>();
        if (colegiadoSelecionado != null) {
            StatusProcesso statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = StatusProcesso.valueOf(status);
                } catch (IllegalArgumentException e) {
                    // Status inválido ignorado
                }
            }

            Aluno aluno = (alunoId != null) ? alunoService.findById(alunoId) : null;
            Professor relator = (relatorId != null) ? professorService.findById(relatorId) : null;

            processos = processoService.findByColegiadoWithFilters(
                    colegiadoSelecionado, statusEnum, aluno, relator, ordenacao);
        }

        // 6. Atributos para a View (Filtros e Dados)
        model.addAttribute("processos", processos);
        model.addAttribute("coordenador", coordenador);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);
        model.addAttribute("isAdmin", isAdmin);

        // Listas para os selects de filtro
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());

        // Manutenção do estado dos filtros na tela (Preservação de IDs)
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
            attr.addFlashAttribute("mensagem", "Processo distribuído com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("mensagemErro", "Erro ao distribuir: " + e.getMessage());
        }

        // Redireciona mantendo o contexto do colegiado que estava sendo visualizado
        if (colegiadoId != null) {
            return "redirect:/coordenador/processo/list?colegiadoId=" + colegiadoId;
        }
        return "redirect:/coordenador/processo/list";
    }
}