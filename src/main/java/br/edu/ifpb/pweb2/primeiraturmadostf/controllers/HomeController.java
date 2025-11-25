package br.edu.ifpb.pweb2.primeiraturmadostf.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.annotation.PostConstruct;


@Controller
@RequestMapping("/home")
public class HomeController {

    @GetMapping
    public String exibirHome(){
        return "auth/login";
    }

    @PostConstruct
public void init() {
    System.out.println("HomeController carregado!");
}




}
