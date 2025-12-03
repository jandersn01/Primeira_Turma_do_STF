package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long alunoId,
            @RequestParam(required = false) Long relatorId,
            @RequestParam(required = false, defaultValue = "desc") String ordenacao) {

        // TODO: Quando implementar autenticação, pegar o coordenador logado
        // Por enquanto, vamos usar o primeiro coordenador como exemplo
        Professor coordenador = professorService.findByCoordenadores().stream()
                .findFirst()
                .orElse(null);

        // Se não houver coordenador, mostrar mensagem
        if (coordenador == null) {
            model.addAttribute("mensagem", 
                "Nenhum coordenador cadastrado no sistema. Por favor, cadastre um coordenador primeiro.");
            return "coordenador/processo/list";
        }

        // Buscar colegiados do coordenador (ou todos se for admin)
        List<Colegiado> colegiados = new ArrayList<>();
        if (coordenador.getColegiados() != null && !coordenador.getColegiados().isEmpty()) {
            colegiados = new ArrayList<>(coordenador.getColegiados());
        } else {
            // Se não houver colegiados específicos, mostrar todos (para desenvolvimento)
            colegiados = colegiadoService.findAll();
        }

        // Se não houver colegiado selecionado e houver colegiados disponíveis, usar o primeiro
        Colegiado colegiadoSelecionado = null;
        if (colegiadoId != null) {
            colegiadoSelecionado = colegiadoService.findById(colegiadoId);
        } else if (!colegiados.isEmpty()) {
            colegiadoSelecionado = colegiados.get(0);
            colegiadoId = colegiadoSelecionado.getId();
        }

        // Lista de processos
        List<Processo> processos = new ArrayList<>();

        if (colegiadoSelecionado != null) {
            // Converter string de status para enum (se fornecido)
            StatusProcesso statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = StatusProcesso.valueOf(status);
                } catch (IllegalArgumentException e) {
                    // Status inválido, ignorar
                }
            }

            // Carregar aluno se fornecido
            Aluno aluno = null;
            if (alunoId != null) {
                aluno = alunoService.findById(alunoId);
            }

            // Carregar relator se fornecido
            Professor relator = null;
            if (relatorId != null) {
                relator = professorService.findById(relatorId);
            }

            // Buscar processos com filtros
            processos = processoService.findByColegiadoWithFilters(
                colegiadoSelecionado, statusEnum, aluno, relator, ordenacao);
        }

        // Adicionar atributos ao model
        model.addAttribute("processos", processos);
        model.addAttribute("coordenador", coordenador);
        model.addAttribute("colegiados", colegiados);
        model.addAttribute("colegiadoSelecionado", colegiadoSelecionado);
        model.addAttribute("alunos", alunoService.findAll());
        model.addAttribute("professores", professorService.findAll());
        model.addAttribute("statusList", StatusProcesso.values());

        // Manter valores dos filtros selecionados
        model.addAttribute("colegiadoIdSelecionado", colegiadoId);
        model.addAttribute("statusSelecionado", status);
        model.addAttribute("alunoIdSelecionado", alunoId);
        model.addAttribute("relatorIdSelecionado", relatorId);
        model.addAttribute("ordenacaoSelecionada", ordenacao);

        // Mensagem informativa se não houver colegiado
        if (colegiadoSelecionado == null) {
            model.addAttribute("mensagem", 
                "Nenhum colegiado encontrado. Por favor, cadastre um colegiado primeiro.");
        }

        return "coordenador/processo/list";
    }
}

