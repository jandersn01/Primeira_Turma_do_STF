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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

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
            @RequestParam(required = false) StatusProcesso status, // O Spring converte String para Enum automaticamente
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long relatorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 1. Identificação de permissões e do usuário logado
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Professor coordenador = professorService.findByMatricula(userDetails.getUsername());

        // 2. Validação de Segurança
        if (!isAdmin && (coordenador == null || !coordenador.getCoordenador())) {
            model.addAttribute("mensagem", "Acesso restrito a coordenadores ou administradores.");
            return "coordenador/processo/list";
        }

        // 3. Configuração da Paginação e Ordenação
        Pageable pageable = PageRequest.of(page, size, Sort.by("numero").descending());

        // 4. Definição da lista de Colegiados (Admin vê todos, Coordenador vê os dele)
        List<Colegiado> colegiados;
        if (isAdmin) {
            colegiados = colegiadoService.findAll();
        } else {
            colegiados = new ArrayList<>(coordenador.getColegiados());
            if (colegiados.isEmpty()) {
                colegiados = colegiadoService.findAll();
            }
        }

        // 5. Determinar o Colegiado selecionado
        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

        // 6. Execução da busca paginada com filtros
        Page<Processo> pagina;
        if (colegiadoSelecionado != null) {
            // Busca os objetos completos para o filtro
            Aluno aluno = (alunoId != null) ? alunoService.findById(alunoId) : null;
            Professor relator = (relatorId != null) ? professorService.findById(relatorId) : null;

            // Aqui usamos o seu service que já lida com todos os filtros de forma paginada
            pagina = processoService.findByColegiadoWithFiltersPaginado(
                    colegiadoSelecionado, status, aluno, relator, pageable);
        } else {
            pagina = Page.empty(pageable);
        }

        // 7. Atributos para a View
        model.addAttribute("pagina", pagina);
        model.addAttribute("processos", pagina.getContent()); // Para manter compatibilidade com seu HTML atual
        model.addAttribute("coordenador", coordenador);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);
        model.addAttribute("isAdmin", isAdmin);

        // Listas para preencher os <select> dos filtros (essencial para o Admin ver as opções)
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());

        // Preservação do estado dos filtros nos campos da tela
        model.addAttribute("colegiadoIdSelecionado", colegiadoId);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("alunoIdSelecionado", alunoId);
        model.addAttribute("relatorIdSelecionado", relatorId);

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
