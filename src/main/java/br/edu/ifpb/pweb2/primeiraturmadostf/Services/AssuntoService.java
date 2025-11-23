package br.edu.ifpb.pweb2.primeiraturmadostf.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.Repository.AssuntoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;

@Service
@Transactional
public class AssuntoService {

private final AssuntoRepository assuntoRepository;

    @Autowired
    public AssuntoService(AssuntoRepository repository){
        this.assuntoRepository = repository;
    }

     public List<Assunto> findAll() {
        return this.assuntoRepository.findAll();
    }

    public Optional<Assunto> findById(Long id) {
        return this.assuntoRepository.findById(id);
    }

    public Optional<Assunto> findByNome(String nome) {
        return this.assuntoRepository.findByNome(nome);
    }

    public Assunto  save(Assunto professor) {
        return assuntoRepository.save(professor);
    }

    public boolean remove(Long id) {
        Optional<Assunto> assunto = this.findById(id);

        if  (assunto.isPresent()) {
            this.assuntoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean removeByNome(String nome) {
        Optional<Assunto> professor = this.findByNome(nome);

        if  (professor.isPresent()) {
            this.assuntoRepository.deleteByNome(nome);
            return true;
        }
        return false;
    }


}
