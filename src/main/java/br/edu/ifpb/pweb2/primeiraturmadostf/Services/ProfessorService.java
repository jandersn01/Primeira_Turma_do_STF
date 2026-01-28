package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Usuario;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.enums.Role;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProfessorRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.UsuarioRepository;


@Service
@Transactional
public class ProfessorService {

    private final ProfessorRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfessorService(ProfessorRepository repository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

     public List<Professor> findAll() {
        return this.repository.findAll();
    }

    public Professor findById(Long id) {
        return this.repository.findById(id)
                .orElse(null);
    }

    public Professor findByMatricula(String matricula) {
        return this.repository.findByMatricula(matricula)
                .orElse(null);
    }

    public Professor save(Professor professor) {
        Professor existente = this.findByMatricula(professor.getMatricula());
        if (existente != null && !existente.getId().equals(professor.getId())) {
            return null;
        }

        String senhaPlana = professor.getSenha();
        boolean isNovoProfessor = (professor.getId() == null);

        // Salva o professor primeiro
        Professor professorSalvo = repository.save(professor);

        // Cria ou atualiza o usuário associado
        if (isNovoProfessor) {
            Usuario usuario = new Usuario();
            usuario.setMatricula(professorSalvo.getMatricula());
            usuario.setSenha(passwordEncoder.encode(senhaPlana));
            usuario.setRole(professorSalvo.getCoordenador() ? Role.ROLE_COORDENADOR : Role.ROLE_PROFESSOR);
            usuario.setAtivo(true);
            usuario.setProfessor(professorSalvo);
            usuarioRepository.save(usuario);
        } else {
            // Se está atualizando, verifica se a senha mudou e atualiza role se necessário
            Usuario usuarioExistente = usuarioRepository.findByMatricula(professorSalvo.getMatricula()).orElse(null);
            if (usuarioExistente != null) {
                // Atualiza role se coordenador mudou
                usuarioExistente.setRole(professorSalvo.getCoordenador() ? Role.ROLE_COORDENADOR : Role.ROLE_PROFESSOR);
                // Atualiza senha se necessário
                if (senhaPlana != null && !senhaPlana.startsWith("$2a$")) {
                    usuarioExistente.setSenha(passwordEncoder.encode(senhaPlana));
                }
                usuarioRepository.save(usuarioExistente);
            }
        }

        return professorSalvo;
    }

    public boolean remove(Long id) {
        Professor professor = this.findById(id);
        if (professor != null) {
            // Remove o usuario associado primeiro
            Usuario usuario = usuarioRepository.findByMatricula(professor.getMatricula()).orElse(null);
            if (usuario != null) {
                usuarioRepository.delete(usuario);
            }
            this.repository.delete(professor);
        }
        return true;
    }

    public boolean removeByMatricula(String matricula) {
        Professor professor = this.findByMatricula(matricula);
        if (professor != null) {
            // Remove o usuário associado primeiro
            Usuario usuario = usuarioRepository.findByMatricula(matricula).orElse(null);
            if (usuario != null) {
                usuarioRepository.delete(usuario);
            }
            this.repository.delete(professor);
        }
        return true;
    }

    public List<Professor> findByCoordenadores() {
        return repository.findByCoordenadorTrue();
    }

    public List<Professor> findAllById(List<Long> list) {
       return repository.findAllById(list);
    }

    public boolean existsByMatricula(String matricula) {
        return this.findByMatricula(matricula) != null;
    }

    public boolean existsByMatriculaAndNotId(String matricula, Long id) {
        Professor professor = this.findByMatricula(matricula);
        return professor != null && !professor.getId().equals(id);
    }

}
