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

import br.edu.ifpb.pweb2.primeiraturmadostf.Services.ProfessorService;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProfessorService professorservice;
    


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
        redirect.addAttribute("mensagem", "Professor salvo com sucesso");
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
        redirect.addAttribute("mensagem", "Registro excluído com sucesso.");
        professorservice.removeByMatricula(matricula);
        return "redirect:/admin/professor/list";
    }

// ------------------------------ -- -- CRUD ALUNO

 
    public String getFormAluno(){
        return "admin/professorform";
    }

  
    public String getAlunoList(){
        return "";
    }

    public String postAluno(){
        return "";
    }

    public String getAlunoById(){
        return "";
    }

    public String deleteAluno(){
        return "";
    }

// ------------------------------ -- -- CRUD COORDENANDOR


    public String getProfessorCoordenandorList(){
        return "";
    }


}
