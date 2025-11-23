package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.primeiraturmadostf.Services.AlunoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.Services.AssuntoService;
import br.edu.ifpb.pweb2.primeiraturmadostf.Services.ProfessorService;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProfessorService professorservice;
    @Autowired
    private AlunoService alunoService;
    @Autowired
    private AssuntoService assuntoService;
    


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
    public String postProfessor(Professor professor, RedirectAttributes redirect){
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
    public String postAluno(Aluno aluno, RedirectAttributes redirect){
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
    public String postAssunto(Assunto assunto, RedirectAttributes redirect){
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



}
