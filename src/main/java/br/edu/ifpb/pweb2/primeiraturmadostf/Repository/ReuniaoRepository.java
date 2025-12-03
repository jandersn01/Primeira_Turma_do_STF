package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
    
    List<Reuniao> findByColegiado(Colegiado colegiado);
    
}

