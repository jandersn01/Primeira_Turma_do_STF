package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long>{

    void deleteByMatricula(String matricula);

    Optional<Professor> findByMatricula(String matricula);

    List<Professor> findByCoordenadorTrue();

}
