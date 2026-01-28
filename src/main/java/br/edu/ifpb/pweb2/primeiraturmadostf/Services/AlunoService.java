package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Usuario;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.enums.Role;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.AlunoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.UsuarioRepository;

@Service
@Transactional
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AlunoService(AlunoRepository alunoRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.alunoRepository = alunoRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Aluno> findAll() {
        return this.alunoRepository.findAll();
    }

    public Aluno findById(Long id) {
        return this.alunoRepository.findById(id)
                .orElse(null);
    }

    public Aluno findByMatricula(String matricula) {
        return this.alunoRepository.findByMatricula(matricula)
                .orElse(null);
    }

    public Aluno save(Aluno aluno) {
        Aluno existente = this.findByMatricula(aluno.getMatricula());
        if (existente != null && !existente.getId().equals(aluno.getId())) {
            return null;
        }

        String senhaPlana = aluno.getSenha();
        boolean isNovoAluno = (aluno.getId() == null);

        // Salva o aluno primeiro
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // Cria ou atualiza o usuário associado
        if (isNovoAluno) {
            Usuario usuario = new Usuario();
            usuario.setMatricula(alunoSalvo.getMatricula());
            usuario.setSenha(passwordEncoder.encode(senhaPlana));
            usuario.setRole(Role.ROLE_ALUNO);
            usuario.setAtivo(true);
            usuario.setAluno(alunoSalvo);
            usuarioRepository.save(usuario);
        } else {
            // Se está atualizando, verifica se a senha mudou (se não está já criptografada)
            Usuario usuarioExistente = usuarioRepository.findByMatricula(alunoSalvo.getMatricula()).orElse(null);
            if (usuarioExistente != null && senhaPlana != null && !senhaPlana.startsWith("$2a$")) {
                usuarioExistente.setSenha(passwordEncoder.encode(senhaPlana));
                usuarioRepository.save(usuarioExistente);
            }
        }

        return alunoSalvo;
    }

    public boolean remove(Long id) {
        Aluno aluno = this.findById(id);
        if (aluno != null) {
            // Remove o usuario associado primeiro
            Usuario usuario = usuarioRepository.findByMatricula(aluno.getMatricula()).orElse(null);
            if (usuario != null) {
                usuarioRepository.delete(usuario);
            }
            this.alunoRepository.delete(aluno);
        }
        return true;
    }

    public boolean removeByMatricula(String matricula) {
        Aluno aluno = this.findByMatricula(matricula);
        if (aluno != null) {
            // Remove o usuário associado primeiro
            Usuario usuario = usuarioRepository.findByMatricula(matricula).orElse(null);
            if (usuario != null) {
                usuarioRepository.delete(usuario);
            }
            this.alunoRepository.delete(aluno);
        }
        return true;
    }

    public boolean existsByMatricula(String matricula) {
        return this.findByMatricula(matricula) != null;
    }

    @Transactional
    public Aluno salvarComUsuario(Aluno aluno) {
        // 1. Salva o aluno primeiro
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // 2. Cria o Usuário de acesso para este aluno
        Usuario novoUsuario = new Usuario();
        novoUsuario.setMatricula(alunoSalvo.getMatricula());
        novoUsuario.setSenha(passwordEncoder.encode("123")); // Senha padrão inicial
        novoUsuario.setRole(Role.ROLE_ALUNO);
        novoUsuario.setAluno(alunoSalvo);
        novoUsuario.setAtivo(true);

        usuarioRepository.save(novoUsuario);
        return alunoSalvo;
    }

    public boolean existsByMatriculaAndNotId(String matricula, Long id) {
        Aluno aluno = this.findByMatricula(matricula);
        return aluno != null && !aluno.getId().equals(id);
    }

}
