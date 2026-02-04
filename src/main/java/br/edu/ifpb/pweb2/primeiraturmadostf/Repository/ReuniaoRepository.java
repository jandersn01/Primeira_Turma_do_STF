package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;

@Repository
public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {

    Page<Reuniao> findAll(Pageable pageable);

    @Query("SELECT r FROM Reuniao r WHERE (:status IS NULL OR r.status = :status)")
    Page<Reuniao> findAllComFiltro(@Param("status") StatusReuniao status, Pageable pageable);

// 2. Altere o método findByMembroIdAndStatus para suportar Pageable
    @Query("SELECT DISTINCT r FROM Reuniao r "
            + "LEFT JOIN r.colegiado c "
            + "LEFT JOIN c.membros m "
            + "WHERE (m.id = :professorId OR :professorId IS NULL) "
            + "AND (:status IS NULL OR r.status = :status)")
    Page<Reuniao> findByMembroIdAndStatus(@Param("professorId") Long professorId, @Param("status") StatusReuniao status, Pageable pageable);

    List<Reuniao> findByColegiado(Colegiado colegiado);

    List<Reuniao> findByColegiadoId(Long colegiadoId);

    Reuniao findReuniaoById(Long id);

    @Query("SELECT r FROM Reuniao r "
            + "LEFT JOIN FETCH r.colegiado c "
            + "LEFT JOIN FETCH c.membros "
            + "LEFT JOIN FETCH r.processos p "
            + "LEFT JOIN FETCH p.interessado "
            + "LEFT JOIN FETCH p.assunto "
            + "LEFT JOIN FETCH p.relator "
            + "LEFT JOIN FETCH r.votos v "
            + "LEFT JOIN FETCH v.professor "
            + "WHERE r.id = :id")
    Reuniao findReuniaoComDetalhesById(@Param("id") Long id);

    List<Reuniao> findByColegiadoIdAndStatus(Long colegiadoId, StatusReuniao status);

    List<Reuniao> findByColegiadoMembrosIdAndStatus(Long professorId, StatusReuniao status);

    List<Reuniao> findByColegiadoMembrosId(Long professorId);

    @Query("SELECT COUNT(r) > 0 FROM Reuniao r WHERE r.status = :status")
    boolean existsByStatus(@Param("status") StatusReuniao status);

    @Query("SELECT r FROM Reuniao r JOIN r.colegiado c JOIN c.membros m WHERE m.id = :professorId AND (:status IS NULL OR r.status = :status)")
    List<Reuniao> findByMembroIdAndStatus(@Param("professorId") Long professorId, @Param("status") StatusReuniao status);

    boolean removeById(Long id);
}
