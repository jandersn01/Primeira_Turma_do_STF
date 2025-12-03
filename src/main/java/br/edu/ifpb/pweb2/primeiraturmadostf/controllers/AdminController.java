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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject.ColegiadoDTO;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.AssuntoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ColegiadoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.services.ProfessorService;
import br.edu.ifpb.pweb2.primeiraturmadostf.validators.ColegiadoValidator;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProfessorService professorservice;
    @Autowired
    private AlunoService alunoService;
    @Autowired
    private AssuntoService assuntoService;
    @Autowired
    private ColegiadoService colegiadoService;
    
    @Autowired
    private ColegiadoValidator colegiadoValidator;
    


// ------------------------------ -- -- CRUD PROFESSOR

    @GetMapping("/professor/form")
    public String getFormProfessor(Model model, Professor professor){
        model.addAttribute("professor", professor);
        return "professor/form";
    }

    @GetMapping("/professor/list")
    public String getProfessorList(Model model){
        model.addAttribute("listaProfessores", professorservice.findAll());
        return "professor/list";
    }

    @PostMapping("/professor/save")
    public String postProfessor(
            @Valid @ModelAttribute("professor") Professor professor,
            BindingResult result,
            RedirectAttributes redirect){
        
        // Validação customizada: matrícula única
        if (professor.getId() == null) {
            // Novo professor - verificar se matrícula já existe
            if (professorservice.existsByMatricula(professor.getMatricula())) {
                result.rejectValue("matricula", "matricula.duplicate", 
                    "Matrícula já cadastrada. Escolha outra matrícula.");
            }
        } else {
            // Professor existente - verificar se matrícula pertence a outro professor
            if (professorservice.existsByMatriculaAndNotId(professor.getMatricula(), professor.getId())) {
                result.rejectValue("matricula", "matricula.duplicate", 
                    "Matrícula já cadastrada para outro professor.");
            }
        }
        
        if (result.hasErrors()) {
            return "professor/form";
        }
        
        professorservice.save(professor);
        redirect.addFlashAttribute("mensagem", "Professor salvo com sucesso");
        return "redirect:/admin/professor/list";
    }

    @GetMapping("/professor/id/{id}")
    public String getProfessorById(Model model, @PathVariable(value="id") Long id){
        model.addAttribute("professor", professorservice.findById(id));
        return "professor/form";
    }

    @GetMapping("/professor/matricula/{matricula}")
    public String getProfessorByMatricula(Model model, @PathVariable(value="matricula") String matricula){
        model.addAttribute("professor", professorservice.findByMatricula(matricula));
        return "professor/form";
    }

    @PostMapping("/professor/delete/{matricula}")
    public String deleteProfessor(@PathVariable(value="matricula") String matricula, RedirectAttributes redirect){
        redirect.addFlashAttribute("mensagem", "Registro excluído com sucesso.");
        professorservice.removeByMatricula(matricula);
        return "redirect:/admin/professor/list";
    }

// ------------------------------ -- -- CRUD ALUNO

    
    @GetMapping("/aluno/form")
    public String getFormAluno(Model model, Aluno aluno){
        model.addAttribute("aluno", aluno);
        return "aluno/form";
    }

    @GetMapping("/aluno/list")
    public String getAlunoList(Model model){
        model.addAttribute("listaAlunos", alunoService.findAll());
        return "aluno/list";
    }

    @PostMapping("/aluno/save")
    public String postAluno(
            @Valid @ModelAttribute("aluno") Aluno aluno,
            BindingResult result,
            RedirectAttributes redirect){
        
        // Validação customizada: matrícula única
        if (aluno.getId() == null) {
            // Novo aluno - verificar se matrícula já existe
            if (alunoService.existsByMatricula(aluno.getMatricula())) {
                result.rejectValue("matricula", "matricula.duplicate", 
                    "Matrícula já cadastrada. Escolha outra matrícula.");
            }
        } else {
            // Aluno existente - verificar se matrícula pertence a outro aluno
            if (alunoService.existsByMatriculaAndNotId(aluno.getMatricula(), aluno.getId())) {
                result.rejectValue("matricula", "matricula.duplicate", 
                    "Matrícula já cadastrada para outro aluno.");
            }
        }
        
        if (result.hasErrors()) {
            return "aluno/form";
        }
        
        alunoService.save(aluno);
        redirect.addFlashAttribute("mensagem", "Aluno cadastrado com sucesso!");
        return "redirect:/admin/aluno/list";
    }

    @GetMapping("aluno/id/{id}")
    public String getAlunoById(Model model, @PathVariable(value="id") Long id){
        model.addAttribute("aluno", alunoService.findById(id));
        return "aluno/form";
    }

    @PostMapping("/aluno/delete/{matricula}")
    public String deleteAluno(RedirectAttributes redirect, @PathVariable(value="matricula")String matricula){
        alunoService.removeByMatricula(matricula);
        redirect.addFlashAttribute("mensagem", "Registro de aluno apagado com sucesso!");
        return "redirect:/admin/aluno/list";
    }

// ------------------------------ -- -- CRUD COORDENANDOR
    //Aqui, poderia ser criado um filtro em professor/list par listar apenas professores;

    @GetMapping("/professor/cordenadore")
    public String getProfessorCoordenandorList(Model model){
        model.addAttribute("coordenadores", professorservice.findByCoordenadores());
        return "professor/list";
    }

// ------------------------------ -- -- CRUD ASSUNTOS

@GetMapping("/assunto/form")
    public String getAssuntoForm(Model model, Assunto assunto){
        model.addAttribute("assunto", assunto);
        return "assunto/form";
    }

    @GetMapping("/assunto/list")
    public String getAssuntoList(Model model){
        model.addAttribute("listaAssuntos", assuntoService.findAll());
        return "assunto/list";
    }

    @PostMapping("/assunto/save")
    public String postAssunto(
            @Valid @ModelAttribute("assunto") Assunto assunto,
            BindingResult result,
            RedirectAttributes redirect){
        
        // Validação customizada: nome único
        if (assunto.getId() == null) {
            // Novo assunto - verificar se nome já existe
            if (assuntoService.existsByNome(assunto.getNome())) {
                result.rejectValue("nome", "nome.duplicate", 
                    "Nome do assunto já cadastrado. Escolha outro nome.");
            }
        } else {
            // Assunto existente - verificar se nome pertence a outro assunto
            if (assuntoService.existsByNomeAndNotId(assunto.getNome(), assunto.getId())) {
                result.rejectValue("nome", "nome.duplicate", 
                    "Nome do assunto já cadastrado para outro assunto.");
            }
        }
        
        if (result.hasErrors()) {
            return "assunto/form";
        }
        
        assuntoService.save(assunto);
        redirect.addFlashAttribute("mensagem", "Assunto cadastrado com sucesso!");
        return "redirect:/admin/assunto/list";
    }

    @GetMapping("assunto/id/{id}")
    public String getAssuntoById(Model model, @PathVariable(value="id") Long id){
        model.addAttribute("assunto", assuntoService.findById(id));
        return "assunto/form";
    }

    @PostMapping("/assunto/delete/{id}")
    public String deleteAssunto(RedirectAttributes redirect, @PathVariable(value="id")Long id){
        assuntoService.remove(id);
        redirect.addFlashAttribute("mensagem", "Registro de assunto apagado com sucesso!");
        return "redirect:/admin/assunto/list";
    }


// ------------------------------ -- -- CRUD COLEGIADO

@GetMapping("/colegiado/form")
    public String getColegiadoForm(Model model){
        ColegiadoDTO dto = new ColegiadoDTO();
        for (int i = 0; i < 5; i++) {
            dto.getMembrosIds().add(null);
        }
        model.addAttribute("colegiado", dto);
        model.addAttribute("professores", professorservice.findAll());
        return "colegiado/form";
    }


    @GetMapping("/colegiado/list")
    public String getColegiadoList(Model model){
        model.addAttribute("listaColegiados", colegiadoService.findAll());
        return "colegiado/list";
    }

    @PostMapping("/colegiado/save")
    public String postColegiado(
            @Valid @ModelAttribute("colegiado") ColegiadoDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirect){
        
        // Executar validações customizadas
        colegiadoValidator.validate(dto, result);
        
        if (result.hasErrors()) {
            // Preparar dados necessários para o formulário
            model.addAttribute("professores", professorservice.findAll());
            return "colegiado/form";
        }
        
        colegiadoService.save(dto);
        redirect.addFlashAttribute("mensagem", "Colegiado cadastrado com sucesso!");
        return "redirect:/admin/colegiado/list";
    }

    @GetMapping("colegiado/id/{id}")
    public String getColegiadoById(Model model, @PathVariable(value="id") Long id){
        model.addAttribute("colegiado", colegiadoService.getForEdit(id));
        model.addAttribute("professores", professorservice.findAll());
        return "colegiado/form";
    }

    @PostMapping("/colegiado/delete/{id}")
    public String deleteColegiado(RedirectAttributes redirect, @PathVariable(value="id")Long id){
        colegiadoService.remove(id);
        redirect.addFlashAttribute("mensagem", "Registro de colegiado apagado com sucesso!");
        return "redirect:/admin/colegiado/list";

    }


}
