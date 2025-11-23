package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/professorform")
    public String getFormProfessor(Model model, Professor professor){
        model.addAttribute("professor", professor);
        return "professor/form";
    }

    @GetMapping("/professorlist")
    public String getProfessorList(Model model){
        model.addAttribute("listaProfessores", professorservice.findAll());
        return "professor/list";
    }

    @PostMapping("/saveprofessor")
    public String postProfessor(Professor professor, RedirectAttributes redirect){
        professorservice.save(professor);
        redirect.addAttribute("mensagem", "Professor salvo com sucesso");
        return "redirect:/admin/professorlist";
    }

    public String getProfessorById(){
        return "";
    }

    public String deleteProfessor(String matricula){
        return "";
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
