package br.edu.ifpb.pweb2.primeiraturmadostf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Usuario;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.enums.Role;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioAdminSeNaoExistir();
    }

    private void criarUsuarioAdminSeNaoExistir() {
        String matriculaAdmin = "admin";

        if (!usuarioRepository.existsByMatricula(matriculaAdmin)) {
            Usuario admin = new Usuario();
            admin.setMatricula(matriculaAdmin);
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setAtivo(true);

            usuarioRepository.save(admin);

            System.out.println("===========================================");
            System.out.println("Usuario ADMIN criado com sucesso!");
            System.out.println("Matricula: admin");
            System.out.println("Senha: admin123");
            System.out.println("===========================================");
        }
    }
}
