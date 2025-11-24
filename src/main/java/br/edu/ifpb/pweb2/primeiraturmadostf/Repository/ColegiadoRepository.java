package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;

@Repository
public interface ColegiadoRepository extends JpaRepository<Colegiado, Long> {

}
