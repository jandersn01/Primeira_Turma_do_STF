package br.edu.ifpb.pweb2.primeiraturmadostf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Curso;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ColegiadoRepository extends JpaRepository<Colegiado, Long> {
    Colegiado findByCursoAndDataFimIsNull(Curso curso);
    
    @Query("SELECT c FROM Colegiado c WHERE c.curso = :curso AND (c.dataFim IS NULL OR c.dataFim >= :hoje)")
    List<Colegiado> findAtivosPorCurso(@Param("curso") Curso curso, @Param("hoje") LocalDate hoje);
}
