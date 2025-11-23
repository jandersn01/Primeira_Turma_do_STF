package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

// ------------------------------ -- -- CRUD PROFESSOR

    @GetMapping("/professorform")
    public String getFormProfessor(){
        return "admin/professorform";
    }

    @GetMapping("/professorlist")
    public String getProfessorList(){
        return "";
    }

    public String postProfessor(){
        return "";
    }

    public String getProfessorById(){
        return "";
    }

    public String deleteProfessor(){
        return "";
    }

// ------------------------------ -- -- CRUD ALUNO

 @GetMapping("/professorform")
    public String getFormAluno(){
        return "admin/professorform";
    }

    @GetMapping("/professorlist")
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

    @GetMapping("/professorlist")
    public String getProfessorCoordenandorList(){
        return "";
    }


}
