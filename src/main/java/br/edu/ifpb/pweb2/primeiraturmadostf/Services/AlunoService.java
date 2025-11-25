package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.AlunoRepository;

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
    return alunoRepository.save(aluno);
    }

    public boolean remove(Long id) {
        Aluno aluno = this.findById(id);
        this.alunoRepository.delete(aluno);
        return true;
    }

    public boolean removeByMatricula(String matricula) {
        Aluno aluno = this.findByMatricula(matricula);
        this.alunoRepository.delete(aluno);
        return true;
    }


}


