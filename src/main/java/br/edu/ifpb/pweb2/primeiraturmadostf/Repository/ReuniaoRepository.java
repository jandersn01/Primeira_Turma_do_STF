package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
    
    List<Reuniao> findByColegiado(Colegiado colegiado);

    // Busca todas as reuniões de um colegiado específico
    List<Reuniao> findByColegiadoId(Long colegiadoId);

    // Busca reuniões de um colegiado filtrando pelo status
    List<Reuniao> findByColegiadoIdAndStatus(Long colegiadoId, StatusReuniao status);
    
}

