package br.edu.ifpb.pweb2.primeiraturmadostf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Usuario;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.enums.Role;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.AlunoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProfessorRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.UsuarioRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        criarUsuarioAdminSeNaoExistir();
        criarUsuariosParaRegistrosExistentes();
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

    private void corrigirSenhasESincronizarUsuarios() {
        // Processa Professores
        professorRepository.findAll().forEach(prof -> {
            // Se a senha no banco não for BCrypt, criptografa
            if (!prof.getSenha().startsWith("$2a$")) {
                prof.setSenha(passwordEncoder.encode(prof.getSenha()));
                professorRepository.save(prof);
            }

            // Garante que o usuário existe e tem a mesma senha
            Usuario user = usuarioRepository.findByMatricula(prof.getMatricula())
                    .orElse(new Usuario());
            
            user.setMatricula(prof.getMatricula());
            user.setSenha(prof.getSenha());
            user.setRole(prof.getCoordenador() ? Role.ROLE_COORDENADOR : Role.ROLE_PROFESSOR);
            user.setProfessor(prof);
            user.setAtivo(true);
            usuarioRepository.save(user);
        });

        // Processa Alunos
        alunoRepository.findAll().forEach(aluno -> {
            if (!aluno.getSenha().startsWith("$2a$")) {
                aluno.setSenha(passwordEncoder.encode(aluno.getSenha()));
                alunoRepository.save(aluno);
            }

            Usuario user = usuarioRepository.findByMatricula(aluno.getMatricula())
                    .orElse(new Usuario());
            
            user.setMatricula(aluno.getMatricula());
            user.setSenha(aluno.getSenha());
            user.setRole(Role.ROLE_ALUNO);
            user.setAluno(aluno);
            user.setAtivo(true);
            usuarioRepository.save(user);
        });
    }

    private void criarUsuariosParaRegistrosExistentes() {
        // Criar usuários para todos os Alunos que não possuem login
        alunoRepository.findAll().forEach(aluno -> {
            if (usuarioRepository.findByMatricula(aluno.getMatricula()).isEmpty()) {
                Usuario user = new Usuario();
                user.setMatricula(aluno.getMatricula());
                user.setSenha(passwordEncoder.encode("123456")); // Senha padrão
                user.setRole(Role.ROLE_ALUNO);
                user.setAluno(aluno);
                usuarioRepository.save(user);
            }
        });

        // Criar usuários para todos os Professores
        professorRepository.findAll().forEach(prof -> {
            if (usuarioRepository.findByMatricula(prof.getMatricula()).isEmpty()) {
                Usuario user = new Usuario();
                user.setMatricula(prof.getMatricula());
                user.setSenha(passwordEncoder.encode("123"));

                // Lógica: se o prof for coordenador no banco, ganha ROLE_COORDENADOR
                if (prof.getCoordenador()) {
                    user.setRole(Role.ROLE_COORDENADOR);
                } else {
                    user.setRole(Role.ROLE_PROFESSOR);
                }

                user.setProfessor(prof);
                usuarioRepository.save(user);
            }
        });
    }
}
