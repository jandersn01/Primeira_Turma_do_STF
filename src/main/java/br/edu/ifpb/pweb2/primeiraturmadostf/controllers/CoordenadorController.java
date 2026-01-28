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
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ReuniaoService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

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

    @Autowired
    private ReuniaoService reuniaoService;

    private boolean isAdmin(UserDetails userDetails) {
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

        boolean admin = isAdmin(userDetails);
        Professor coordenador = professorService.findByMatricula(userDetails.getUsername());

        // Admin tem acesso mesmo sem ser coordenador
        if (!admin && (coordenador == null || !coordenador.getCoordenador())) {
            model.addAttribute("mensagem", "Voce nao tem permissao de coordenador.");
            return "coordenador/processo/list";
        }

        model.addAttribute("isAdmin", admin);

        List<Colegiado> colegiados;
        if (admin) {
            // Admin ve todos os colegiados
            colegiados = colegiadoService.findAll();
        } else {
            colegiados = new ArrayList<>(coordenador.getColegiados());
            if (colegiados.isEmpty()) {
                colegiados = colegiadoService.findAll();
            }
        }

        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

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

        model.addAttribute("processos", processos);
        model.addAttribute("coordenador", coordenador);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);

        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());

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

    // ========== ENDPOINTS DE REUNIAO ==========

    @GetMapping("/reuniao/list")
    public String listarReunioes(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Long colegiadoId) {

        boolean admin = isAdmin(userDetails);
        Professor coordenador = professorService.findByMatricula(userDetails.getUsername());

        // Admin tem acesso mesmo sem ser coordenador
        if (!admin && (coordenador == null || !coordenador.getCoordenador())) {
            model.addAttribute("mensagemErro", "Voce nao tem permissao de coordenador.");
            return "coordenador/reuniao/list";
        }

        model.addAttribute("isAdmin", admin);

        List<Colegiado> colegiados;
        if (admin) {
            colegiados = colegiadoService.findAll();
        } else {
            colegiados = new ArrayList<>(coordenador.getColegiados());
            if (colegiados.isEmpty()) {
                colegiados = colegiadoService.findAll();
            }
        }

        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

        List<Reuniao> reunioes = new ArrayList<>();
        if (colegiadoSelecionado != null) {
            reunioes = reuniaoService.findByColegiado(colegiadoSelecionado);
        }

        // Verifica se existe sessao em andamento
        boolean existeSessaoEmAndamento = reuniaoService.existsSessaoEmAndamento();

        model.addAttribute("coordenador", coordenador);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);
        model.addAttribute("colegiadoIdSelecionado", colegiadoId);
        model.addAttribute("reunioes", reunioes);
        model.addAttribute("existeSessaoEmAndamento", existeSessaoEmAndamento);
        model.addAttribute("statusList", StatusReuniao.values());

        return "coordenador/reuniao/list";
    }

    @PostMapping("/reuniao/iniciar")
    public String iniciarSessao(
            @RequestParam("reuniaoId") Long reuniaoId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes attr) {

        try {
            Reuniao reuniao = reuniaoService.iniciarSessao(reuniaoId, userDetails.getUsername());
            attr.addFlashAttribute("mensagem", "Sessao iniciada com sucesso!");
            return "redirect:/coordenador/reuniao/" + reuniaoId + "/conduzir";
        } catch (IllegalStateException e) {
            attr.addFlashAttribute("mensagemErro", e.getMessage());
        } catch (Exception e) {
            attr.addFlashAttribute("mensagemErro", "Erro ao iniciar sessao: " + e.getMessage());
        }

        return "redirect:/coordenador/reuniao/list";
    }

    @GetMapping("/reuniao/{id}/conduzir")
    public String conduzirSessao(
            @PathVariable("id") Long reuniaoId,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes attr) {

        boolean admin = isAdmin(userDetails);
        Professor coordenador = professorService.findByMatricula(userDetails.getUsername());

        // Admin tem acesso mesmo sem ser coordenador
        if (!admin && (coordenador == null || !coordenador.getCoordenador())) {
            attr.addFlashAttribute("mensagemErro", "Voce nao tem permissao de coordenador.");
            return "redirect:/coordenador/reuniao/list";
        }

        Reuniao reuniao = reuniaoService.findById(reuniaoId);
        if (reuniao == null) {
            attr.addFlashAttribute("mensagemErro", "Reuniao nao encontrada.");
            return "redirect:/coordenador/reuniao/list";
        }

        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            attr.addFlashAttribute("mensagemErro", "Esta reuniao nao esta em andamento.");
            return "redirect:/coordenador/reuniao/list";
        }

        model.addAttribute("isAdmin", admin);
        model.addAttribute("coordenador", coordenador);
        model.addAttribute("reuniao", reuniao);
        model.addAttribute("processos", reuniao.getProcessos());
        model.addAttribute("membros", reuniao.getColegiado().getMembros());

        return "coordenador/reuniao/conduzir";
    }

    @PostMapping("/reuniao/encerrar")
    public String encerrarSessao(
            @RequestParam("reuniaoId") Long reuniaoId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes attr) {

        try {
            reuniaoService.encerrarSessao(reuniaoId, userDetails.getUsername());
            attr.addFlashAttribute("mensagem", "Sessao encerrada com sucesso!");
        } catch (IllegalStateException e) {
            attr.addFlashAttribute("mensagemErro", e.getMessage());
        } catch (Exception e) {
            attr.addFlashAttribute("mensagemErro", "Erro ao encerrar sessao: " + e.getMessage());
        }

        return "redirect:/coordenador/reuniao/list";
    }
}