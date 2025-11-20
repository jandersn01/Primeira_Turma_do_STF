package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String redirecioanemtoRaiz(){
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String exibirHome(){
        return "layout/home";
    }



}
