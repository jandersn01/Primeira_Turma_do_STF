package br.edu.ifpb.pweb2.primeiraturmadostf.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.Repository.ProfessorRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;

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

    public Optional<Professor> findById(Long id) {
        return this.repository.findById(id);
    }

    public Optional<Professor> findByMatricula(String matricula) {
        return this.repository.findByMatricula(matricula);
    }

    public Professor save(Professor professor) {
        return repository.save(professor);
    }

    public boolean remove(Long id) {
        Optional<Professor> student = this.findById(id);

        if  (student.isPresent()) {
            this.repository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean removeByMatricula(String matricula) {
        Optional<Professor> student = this.findByMatricula(matricula);

        if  (student.isPresent()) {
            this.repository.deleteByMatricula(matricula);
            return true;
        }
        return false;
    }

    public List<Professor> findByCoordenadores() {
        return repository.findByCoordenadorTrue();
    }


}
