package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Curso;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.CursoRepository;

@Service
@Transactional
public class CursoService {

    private final CursoRepository cursoRepository;

    @Autowired
    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> findAll() {
        return this.cursoRepository.findAll();
    }

    public Curso findById(Long id) {
        return this.cursoRepository.findById(id)
                .orElse(null);
    }

    public Curso findByNome(String nome) {
        return this.cursoRepository.findByNome(nome);
               // .orElse(null);
    }

    public Curso save(Curso curso) {
        Curso existente = this.findByNome(curso.getNome());

        // Impede salvar se já existir outro curso com o mesmo nome
        if (existente != null && !existente.getId().equals(curso.getId())) {
            return null;
        }

        return this.cursoRepository.save(curso);
    }

    public boolean remove(Long id) {
        Curso curso = this.findById(id);
        if (curso == null) return false;

        this.cursoRepository.delete(curso);
        return true;
    }

    public boolean removeByNome(String nome) {
        Curso curso = this.findByNome(nome);
        if (curso == null) return false;

        this.cursoRepository.delete(curso);
        return true;
    }

    public boolean existsByNome(String nome) {
        return this.findByNome(nome) != null;
    }

    public boolean existsByNomeAndNotId(String nome, Long id) {
        Curso curso = this.findByNome(nome);
        return curso != null && !curso.getId().equals(id);
    }
}
