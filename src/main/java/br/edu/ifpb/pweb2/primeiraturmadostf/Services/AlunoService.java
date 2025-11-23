package br.edu.ifpb.pweb2.primeiraturmadostf.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.Repository.AlunoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;

@Service
@Transactional
public class AlunoService {

    private final AlunoRepository alunoRepository;

    @Autowired
    public AlunoService(AlunoRepository alunoRepository){
        this.alunoRepository = alunoRepository;
    }

     public List<Aluno> findAll() {
        return this.alunoRepository.findAll();
    }

    public Optional<Aluno> findById(Long id) {
        return this.alunoRepository.findById(id);
    }

    public Optional<Aluno> findByMatricula(String matricula) {
        return this.alunoRepository.findByMatricula(matricula);
    }

    public Aluno  save(Aluno professor) {
        return alunoRepository.save(professor);
    }

    public boolean remove(Long id) {
        Optional<Aluno> student = this.findById(id);

        if  (student.isPresent()) {
            this.alunoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean removeByMatricula(String matricula) {
        Optional<Aluno> professor = this.findByMatricula(matricula);

        if  (professor.isPresent()) {
            this.alunoRepository.deleteByMatricula(matricula);
            return true;
        }
        return false;
    }


}


