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
    
    Reuniao findReuniaoById(Long id);

    // Busca reuniões de um colegiado filtrando pelo status
    List<Reuniao> findByColegiadoIdAndStatus(Long colegiadoId, StatusReuniao status);

    // Busca reuniões dos colegiados onde o professor é membro, filtrando por status
    List<Reuniao> findByColegiadoMembrosIdAndStatus(Long professorId, StatusReuniao status);
    
    List<Reuniao> findByColegiadoMembrosId(Long professorId);

    public boolean removeById(Long id);
    
}

