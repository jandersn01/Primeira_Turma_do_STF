package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // IMPORT CORRETO
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProcessoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ReuniaoService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/reunioes")
public class ReuniaoController {

    @Autowired
    ReuniaoService reuniaoService;

    @Autowired
    ColegiadoService colegiadoService;

    @Autowired
    ProfessorService professorService;

    @Autowired
    ProcessoService processoService;

    @GetMapping("/form")
    public String reuniaoForm(
            Model model,
            @RequestParam(required = false) Long colegiadoId) {

        List<Colegiado> colegiados = colegiadoService.findAll();
        model.addAttribute("colegiados", colegiados);

        if (colegiadoId == null && !colegiados.isEmpty()) {
            colegiadoId = colegiados.get(0).getId();
        }

        model.addAttribute("reuniao", new Reuniao());
        model.addAttribute("colegiadoIdSelecionado", colegiadoId);

        if (colegiadoId != null) {
            List<Processo> processosDisponiveis = reuniaoService.listarProcessosDisponiveisParaReuniao(colegiadoId);
            model.addAttribute("processosDisponiveis", processosDisponiveis);
        } else {
            model.addAttribute("processosDisponiveis", new ArrayList<>());
        }

        return "reuniao/form";
    }

    @GetMapping
    public String listarReunioes(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "status", required = false) String statusStr,
            @PageableDefault(size = 10) Pageable pageable) {

        StatusReuniao status = null;
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                status = StatusReuniao.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                status = null;
            }
        }

        Professor professorLogado = professorService.findByMatricula(userDetails.getUsername());
        boolean isCoordenadorOuAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_COORDENADOR") || a.getAuthority().equals("ROLE_ADMIN"));

        Page<Reuniao> paginaReunioes;
        if (isCoordenadorOuAdmin) {
            paginaReunioes = reuniaoService.listarTodasPaginadas(status, pageable);
        } else {
            paginaReunioes = reuniaoService.listarReunioesDoProfessorPaginadas(professorLogado.getId(), status, pageable);
            model.addAttribute("isProfessorView", true); // Garante que o HTML saiba que é visão de professor
        }

        model.addAttribute("pagina", paginaReunioes);
        model.addAttribute("reunioes", paginaReunioes.getContent());

        model.addAttribute("statusSelecionado", status);
        model.addAttribute("professorLogado", professorLogado);

        return "reuniao/list";
    }

    @PostMapping("/save")
    public String criarReuniao(
            Reuniao reuniao,
            @RequestParam(name = "processosIds", required = false) List<Long> processosIds,
            @RequestParam(name = "colegiadoId") Long colegiadoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validação manual dos campos do formulário
        boolean hasErrors = false;

        if (reuniao.getDataReuniao() == null) {
            model.addAttribute("erroData", "A data da reunião é obrigatória");
            hasErrors = true;
        } else if (reuniao.getDataReuniao().isBefore(java.time.LocalDate.now())) {
            model.addAttribute("erroData", "A data da reunião não pode ser no passado");
            hasErrors = true;
        }

        if (colegiadoId == null) {
            model.addAttribute("erroColegiado", "Selecione um colegiado");
            hasErrors = true;
        }

        if (processosIds == null || processosIds.isEmpty()) {
            model.addAttribute("erroProcessos", "Selecione pelo menos um processo para a pauta");
            hasErrors = true;
        }

        if (hasErrors) {
            List<Colegiado> colegiados = colegiadoService.findAll();
            model.addAttribute("colegiados", colegiados);
            model.addAttribute("colegiadoIdSelecionado", colegiadoId);
            model.addAttribute("reuniao", reuniao);
            if (colegiadoId != null) {
                List<Processo> processosDisponiveis = reuniaoService.listarProcessosDisponiveisParaReuniao(colegiadoId);
                model.addAttribute("processosDisponiveis", processosDisponiveis);
            }
            return "reuniao/form";
        }

        try {
            reuniaoService.criarReuniao(reuniao, processosIds, colegiadoId);
            redirectAttributes.addFlashAttribute("mensagem", "Reunião agendada com sucesso!");
            return "redirect:/reunioes?colegiadoId=" + colegiadoId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar: " + e.getMessage());
            return "redirect:/reunioes/form?colegiadoId=" + colegiadoId;
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

    // ========== RF10: INICIAR SESSÃO ==========
    @PostMapping("/{id}/iniciar")
    public String iniciarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Reuniao reuniao = reuniaoService.iniciarSessao(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("mensagem", "Sessão iniciada com sucesso!");
            return "redirect:/reunioes/" + id + "/conduzir";

        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao iniciar sessão: " + e.getMessage());
        }

        return "redirect:/reunioes";
    }

    @GetMapping("/{id}/conduzir")
    public String conduzirSessao(
            @PathVariable Long id,
            @RequestParam(name = "processoId", required = false) Long processoId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Reuniao reuniao = reuniaoService.findByIdComDetalhes(id);

        if (reuniao == null) {
            redirectAttributes.addFlashAttribute("erro", "Reunião não encontrada.");
            return "redirect:/reunioes";
        }

        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            redirectAttributes.addFlashAttribute("erro", "Esta reunião não está em andamento.");
            return "redirect:/reunioes";
        }

        model.addAttribute("reuniao", reuniao);
        model.addAttribute("processos", reuniao.getProcessos());
        model.addAttribute("membros", reuniao.getColegiado().getMembros());

        // Processo selecionado para apreciacao
        if (processoId != null) {
            Processo processoSelecionado = processoService.findById(processoId);
            if (processoSelecionado != null && reuniao.getProcessos().contains(processoSelecionado)) {
                model.addAttribute("processoSelecionado", processoSelecionado);

                // Carrega votos ja registrados para este processo nesta reuniao
                Map<Long, String> votosRegistrados = new HashMap<>();
                for (Voto voto : reuniao.getVotos()) {
                    if (voto.getProcesso().getId().equals(processoId)) {
                        if (voto.getAusente()) {
                            votosRegistrados.put(voto.getProfessor().getId(), "AUSENTE");
                        } else if (voto.getTipo() != null) {
                            votosRegistrados.put(voto.getProfessor().getId(), voto.getTipo().name());
                        }
                    }
                }
                model.addAttribute("votosRegistrados", votosRegistrados);
            }
        }

        return "reuniao/conduzir";
    }

    @PostMapping("/{reuniaoId}/votar")
    public String registrarVotos(
            @PathVariable Long reuniaoId,
            @RequestParam Long processoId,
            @RequestParam String acao,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            Reuniao reuniao = reuniaoService.findById(reuniaoId);
            if (reuniao == null || reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
                redirectAttributes.addFlashAttribute("erro", "Reunião não está em andamento.");
                return "redirect:/reunioes";
            }

            Processo processo = processoService.findById(processoId);
            if (processo == null || !reuniao.getProcessos().contains(processo)) {
                redirectAttributes.addFlashAttribute("erro", "Processo não encontrado na pauta.");
                return "redirect:/reunioes/" + reuniaoId + "/conduzir";
            }

            if ("retirar".equals(acao)) {
                reuniaoService.retirarProcessoDaPauta(reuniaoId, processoId);
                redirectAttributes.addFlashAttribute("mensagem", "Processo retirado da pauta.");
                return "redirect:/reunioes/" + reuniaoId + "/conduzir";
            }

            // Coleta os votos do formulario
            Map<Long, String> votos = new HashMap<>();
            for (Professor membro : reuniao.getColegiado().getMembros()) {
                String votoValue = request.getParameter("voto_" + membro.getId());
                if (votoValue != null && !votoValue.isEmpty()) {
                    votos.put(membro.getId(), votoValue);
                }
            }

            reuniaoService.registrarVotos(reuniaoId, processoId, votos);
            redirectAttributes.addFlashAttribute("mensagem", "Votos registrados com sucesso!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar votos: " + e.getMessage());
        }

        return "redirect:/reunioes/" + reuniaoId + "/conduzir?processoId=" + processoId;
    }

    @PostMapping("/{id}/encerrar")
    public String encerrarSessao(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            reuniaoService.encerrarSessao(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("mensagem", "Sessão encerrada com sucesso!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao encerrar sessão: " + e.getMessage());
        }

        return "redirect:/reunioes";
    }
}
