package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProfessorRepository;


@Service
@Transactional
public class ProfessorService {

    private final ProfessorRepository repository;
    
    @Autowired
    public ProfessorService(ProfessorRepository repository) {
        this.repository = repository;
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
        return repository.save(professor);
    }

    public boolean remove(Long id) {
        Professor professor = this.findById(id);
        this.repository.delete(professor);
        return true;
    }

    public boolean removeByMatricula(String matricula) {
        Professor professor = this.findByMatricula(matricula);
        this.repository.delete(professor);
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
