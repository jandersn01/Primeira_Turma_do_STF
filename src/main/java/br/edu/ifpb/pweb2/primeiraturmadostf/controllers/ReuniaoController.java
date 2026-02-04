package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.TipoDecisao;
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

        // Processo selecionado para apreciacao
        if (processoId != null) {
            Processo processoSelecionado = processoService.findById(processoId);
            if (processoSelecionado != null && reuniao.getProcessos().contains(processoSelecionado)) {
                model.addAttribute("processoSelecionado", processoSelecionado);

                // Filtra membros excluindo o relator (ele já deu parecer)
                Set<Professor> membrosParaVotar = reuniao.getColegiado().getMembros().stream()
                        .filter(m -> processoSelecionado.getRelator() == null ||
                                     !m.getId().equals(processoSelecionado.getRelator().getId()))
                        .collect(Collectors.toSet());
                model.addAttribute("membros", membrosParaVotar);

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
        } else {
            // Se nenhum processo selecionado, mostra todos os membros
            model.addAttribute("membros", reuniao.getColegiado().getMembros());
        }

        return "reuniao/conduzir";
    }

    /**
     * Apregoa um processo para julgamento.
     * Muda o status do processo de EM_PAUTA para EM_JULGAMENTO.
     */
    @PostMapping("/{reuniaoId}/processo/{processoId}/apregoar")
    public String apregoarProcesso(
            @PathVariable Long reuniaoId,
            @PathVariable Long processoId,
            RedirectAttributes redirectAttributes) {

        try {
            reuniaoService.apregoarProcesso(reuniaoId, processoId);
            redirectAttributes.addFlashAttribute("mensagem", "Processo apregoado para julgamento!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao apregoar processo: " + e.getMessage());
        }

        return "redirect:/reunioes/" + reuniaoId + "/conduzir?processoId=" + processoId;
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
                // Só pode retirar se não estiver JULGADO
                if (processo.getStatus() == StatusProcesso.JULGADO) {
                    redirectAttributes.addFlashAttribute("erro", "Não é possível retirar um processo já julgado.");
                    return "redirect:/reunioes/" + reuniaoId + "/conduzir?processoId=" + processoId;
                }
                reuniaoService.retirarProcessoDaPauta(reuniaoId, processoId);
                redirectAttributes.addFlashAttribute("mensagem", "Processo retirado da pauta.");
                return "redirect:/reunioes/" + reuniaoId + "/conduzir";
            }

            if ("concluir".equals(acao)) {
                // Verifica se processo está em julgamento
                if (processo.getStatus() != StatusProcesso.EM_JULGAMENTO) {
                    redirectAttributes.addFlashAttribute("erro", "Processo precisa estar apregoado (em julgamento) para ser concluído.");
                    return "redirect:/reunioes/" + reuniaoId + "/conduzir?processoId=" + processoId;
                }

                // Filtra membros excluindo o relator (ele já deu parecer)
                Set<Professor> membrosParaVotar = reuniao.getColegiado().getMembros().stream()
                        .filter(m -> processo.getRelator() == null ||
                                     !m.getId().equals(processo.getRelator().getId()))
                        .collect(Collectors.toSet());

                // Coleta os votos do formulário (apenas dos membros que devem votar)
                Map<Long, String> votos = new HashMap<>();
                for (Professor membro : membrosParaVotar) {
                    String votoValue = request.getParameter("voto_" + membro.getId());
                    if (votoValue != null && !votoValue.isEmpty()) {
                        votos.put(membro.getId(), votoValue);
                    }
                }

                // Verifica se todos os membros (exceto relator) votaram
                if (votos.size() < membrosParaVotar.size()) {
                    redirectAttributes.addFlashAttribute("erro", "Todos os membros devem votar antes de concluir o julgamento.");
                    // Salva os votos parciais mesmo assim
                    reuniaoService.registrarVotos(reuniaoId, processoId, votos);
                    return "redirect:/reunioes/" + reuniaoId + "/conduzir?processoId=" + processoId;
                }

                // Conclui o julgamento e calcula o resultado
                TipoDecisao resultado = reuniaoService.concluirJulgamento(reuniaoId, processoId, votos);
                redirectAttributes.addFlashAttribute("mensagem",
                    "Julgamento concluído! Resultado: " + resultado.getTitulo());

                return "redirect:/reunioes/" + reuniaoId + "/conduzir";
            }

            // Ação padrão: apenas salvar votos (sem concluir)
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
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar votos: " + e.getMessage());
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

    // ========== VOTAÇÃO INDIVIDUAL DO PROFESSOR ==========

    /**
     * Tela de participação do professor na sessão.
     * Exibe os processos da pauta e permite votar individualmente.
     */
    @GetMapping("/{id}/participar")
    public String participarSessao(
            @PathVariable Long id,
            @RequestParam(name = "processoId", required = false) Long processoId,
            @AuthenticationPrincipal UserDetails userDetails,
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

        // Busca o professor logado
        Professor professorLogado = professorService.findByMatricula(userDetails.getUsername());
        if (professorLogado == null) {
            redirectAttributes.addFlashAttribute("erro", "Professor não encontrado.");
            return "redirect:/reunioes";
        }

        // Verifica se professor é membro do colegiado
        if (!reuniao.getColegiado().getMembros().contains(professorLogado)) {
            redirectAttributes.addFlashAttribute("erro", "Você não é membro do colegiado desta reunião.");
            return "redirect:/reunioes";
        }

        model.addAttribute("reuniao", reuniao);
        model.addAttribute("processos", reuniao.getProcessos());
        model.addAttribute("professorLogado", professorLogado);

        // Busca votos já registrados pelo professor nesta reunião
        java.util.List<br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto> votosDoProfessor =
            reuniaoService.getVotosDoProfessorNaReuniao(id, professorLogado.getId());

        // Mapa de processoId -> voto do professor
        Map<Long, br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto> votosMap = new HashMap<>();
        for (br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto v : votosDoProfessor) {
            votosMap.put(v.getProcesso().getId(), v);
        }
        model.addAttribute("meuVotosPorProcesso", votosMap);

        // Processo selecionado para votação
        if (processoId != null) {
            Processo processoSelecionado = processoService.findById(processoId);
            // Verifica se o processo pertence à pauta (por ID)
            boolean pertencePauta = reuniao.getProcessos().stream()
                    .anyMatch(p -> p.getId().equals(processoId));

            if (processoSelecionado != null && pertencePauta) {
                model.addAttribute("processoSelecionado", processoSelecionado);

                // Verifica se já votou neste processo
                br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto meuVoto = votosMap.get(processoId);
                model.addAttribute("meuVoto", meuVoto);
            }
        }

        return "reuniao/participar";
    }

    /**
     * Registra o voto individual do professor em um processo.
     */
    @PostMapping("/{reuniaoId}/processo/{processoId}/votar")
    public String registrarVotoIndividual(
            @PathVariable Long reuniaoId,
            @PathVariable Long processoId,
            @RequestParam String tipoVoto,
            @RequestParam(required = false) String justificativa,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Professor professorLogado = professorService.findByMatricula(userDetails.getUsername());
            if (professorLogado == null) {
                redirectAttributes.addFlashAttribute("erro", "Professor não encontrado.");
                return "redirect:/reunioes";
            }

            reuniaoService.registrarVotoIndividual(reuniaoId, processoId, professorLogado.getId(),
                                                    tipoVoto, justificativa);

            redirectAttributes.addFlashAttribute("mensagem", "Voto registrado com sucesso!");

        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao registrar voto: " + e.getMessage());
        }

        return "redirect:/reunioes/" + reuniaoId + "/participar?processoId=" + processoId;
    }
}
