package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    /**
     * Verifica se um professor já votou em um processo específico em uma reunião.
     */
    boolean existsByProfessorAndProcessoAndReuniao(Professor professor, Processo processo, Reuniao reuniao);

    /**
     * Busca o voto de um professor em um processo específico de uma reunião.
     */
    Optional<Voto> findByProfessorAndProcessoAndReuniao(Professor professor, Processo processo, Reuniao reuniao);

    /**
     * Busca todos os votos de um professor em uma reunião.
     */
    List<Voto> findByProfessorAndReuniao(Professor professor, Reuniao reuniao);

    /**
     * Busca todos os votos de um processo em uma reunião.
     */
    List<Voto> findByProcessoAndReuniao(Processo processo, Reuniao reuniao);

    /**
     * Conta quantos votos foram registrados para um processo em uma reunião.
     */
    long countByProcessoAndReuniao(Processo processo, Reuniao reuniao);

    /**
     * Busca votos por IDs.
     */
    @Query("SELECT v FROM Voto v WHERE v.professor.id = :professorId AND v.processo.id = :processoId AND v.reuniao.id = :reuniaoId")
    Optional<Voto> findByIds(@Param("professorId") Long professorId, @Param("processoId") Long processoId, @Param("reuniaoId") Long reuniaoId);
}
