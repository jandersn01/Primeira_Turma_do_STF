package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Usuario;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.enums.Role;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario findByMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula).orElse(null);
    }

    public boolean existsByMatricula(String matricula) {
        return usuarioRepository.existsByMatricula(matricula);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuarioParaAluno(Aluno aluno, String senhaPlana) {
        Usuario usuario = new Usuario();
        usuario.setMatricula(aluno.getMatricula());
        usuario.setSenha(passwordEncoder.encode(senhaPlana));
        usuario.setRole(Role.ROLE_ALUNO);
        usuario.setAtivo(true);
        usuario.setAluno(aluno);
        return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuarioParaProfessor(Professor professor, String senhaPlana) {
        Usuario usuario = new Usuario();
        usuario.setMatricula(professor.getMatricula());
        usuario.setSenha(passwordEncoder.encode(senhaPlana));
        usuario.setRole(professor.getCoordenador() ? Role.ROLE_COORDENADOR : Role.ROLE_PROFESSOR);
        usuario.setAtivo(true);
        usuario.setProfessor(professor);
        return usuarioRepository.save(usuario);
    }

    public Usuario criarUsuarioAdmin(String matricula, String senhaPlana) {
        Usuario usuario = new Usuario();
        usuario.setMatricula(matricula);
        usuario.setSenha(passwordEncoder.encode(senhaPlana));
        usuario.setRole(Role.ROLE_ADMIN);
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    public void atualizarSenha(Usuario usuario, String novaSenhaPlana) {
        usuario.setSenha(passwordEncoder.encode(novaSenhaPlana));
        usuarioRepository.save(usuario);
    }

    public void atualizarRoleProfessor(Professor professor) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByMatricula(professor.getMatricula());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setRole(professor.getCoordenador() ? Role.ROLE_COORDENADOR : Role.ROLE_PROFESSOR);
            usuarioRepository.save(usuario);
        }
    }

    public void delete(Long id) {
        usuarioRepository.deleteById(id);
    }

    public void deleteByMatricula(String matricula) {
        Usuario usuario = findByMatricula(matricula);
        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }
}
