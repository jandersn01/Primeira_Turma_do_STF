package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.AssuntoRepository;

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

  public Assunto findById(Long id) {
        return this.assuntoRepository.findById(id)
                .orElse(null);
    }

    public Assunto findByNome(String nome) {
        return this.assuntoRepository.findByNome(nome)
                .orElse(null);
    }

    public Assunto save(Assunto assunto) {
        Assunto existente = this.findByNome(assunto.getNome());
        if(existente != null && !existente.getId().equals(assunto.getId())){
            return null;
        }
        return assuntoRepository.save(assunto);
    }

    public boolean remove(Long id) {
        Assunto assunto = this.findById(id);
        this.assuntoRepository.delete(assunto);
        return true;
    }

    public boolean removeByNome(String nome) {
        Assunto assunto = this.findByNome(nome);
        this.assuntoRepository.delete(assunto);
        return true;
    }

    public boolean existsByNome(String nome) {
        return this.findByNome(nome) != null;
    }

    public boolean existsByNomeAndNotId(String nome, Long id) {
        Assunto assunto = this.findByNome(nome);
        return assunto != null && !assunto.getId().equals(id);
    }

}
