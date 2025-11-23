package br.edu.ifpb.pweb2.primeiraturmadostf.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;

@Repository
public interface AssuntoRepository extends JpaRepository<Assunto, Long> {

    public void deleteByNome(String matricula);

    public Optional<Assunto> findByNome(String nome);
}
