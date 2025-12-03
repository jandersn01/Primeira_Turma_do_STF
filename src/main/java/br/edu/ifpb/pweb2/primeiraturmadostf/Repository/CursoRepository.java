package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Curso;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long>{

    public Curso findByNome(String nome);

}
