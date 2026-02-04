package br.edu.ifpb.pweb2.primeiraturmadostf.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para construir Specifications dinâmicas para consultas de Processo.
 * Permite criar queries flexíveis combinando filtros opcionais.
 */
public class ProcessoSpecifications {

    /**
     * Filtra processos por aluno interessado.
     * Se o aluno for null, não aplica filtro (retorna conjunction).
     */
    public static Specification<Processo> byInteressado(Aluno aluno) {
        return (root, query, cb) -> {
            if (aluno == null) {
                return cb.conjunction(); // sempre verdadeiro, não filtra
            }
            return cb.equal(root.get("interessado"), aluno);
        };
    }

    /**
     * Filtra processos por status.
     * Se o status for null, não aplica filtro (retorna conjunction).
     */
    public static Specification<Processo> byStatus(StatusProcesso status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction(); // sempre verdadeiro, não filtra
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filtra processos por assunto.
     * Se o assunto for null, não aplica filtro (retorna conjunction).
     */
    public static Specification<Processo> byAssunto(Assunto assunto) {
        return (root, query, cb) -> {
            if (assunto == null) {
                return cb.conjunction(); // sempre verdadeiro, não filtra
            }
            return cb.equal(root.get("assunto"), assunto);
        };
    }

    /**
     * Filtra processos por relator (Professor).
     * Se o relator for null, não aplica filtro (retorna conjunction).
     */
    public static Specification<Processo> byRelator(Professor relator) {
        return (root, query, cb) -> {
            if (relator == null) {
                return cb.conjunction(); // sempre verdadeiro, não filtra
            }
            return cb.equal(root.get("relator"), relator);
        };
    }

    /**
     * Filtra processos por colegiado.
     * Se o colegiado for null, não aplica filtro (retorna conjunction).
     */
    public static Specification<Processo> byColegiado(Colegiado colegiado) {
        return (root, query, cb) -> {
            if (colegiado == null) {
                return cb.conjunction(); // sempre verdadeiro, não filtra
            }
            return cb.equal(root.get("colegiado"), colegiado);
        };
    }

    /**
     * Constrói uma Specification combinando todos os filtros fornecidos.
     * Os filtros são combinados com AND.
     *
     * @param aluno Aluno interessado (pode ser null para não filtrar)
     * @param status Status do processo (pode ser null para não filtrar)
     * @param assunto Assunto do processo (pode ser null para não filtrar)
     * @return Specification combinada com todos os filtros aplicáveis
     */
    public static Specification<Processo> buildSpecification(
            Aluno aluno, StatusProcesso status, Assunto assunto) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (aluno != null) {
                predicates.add(cb.equal(root.get("interessado"), aluno));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (assunto != null) {
                predicates.add(cb.equal(root.get("assunto"), assunto));
            }

            // Se não há predicados, retorna todos os registros
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Constrói uma Specification combinando todos os filtros fornecidos para processos do colegiado.
     * Os filtros são combinados com AND.
     *
     * @param colegiado Colegiado (pode ser null para não filtrar)
     * @param status Status do processo (pode ser null para não filtrar)
     * @param aluno Aluno interessado (pode ser null para não filtrar)
     * @param relator Professor relator (pode ser null para não filtrar)
     * @return Specification combinada com todos os filtros aplicáveis
     */
    public static Specification<Processo> buildSpecificationForColegiado(
            Colegiado colegiado, StatusProcesso status, Aluno aluno, Professor relator) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (colegiado != null) {
                predicates.add(cb.equal(root.get("colegiado"), colegiado));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (aluno != null) {
                predicates.add(cb.equal(root.get("interessado"), aluno));
            }

            if (relator != null) {
                predicates.add(cb.equal(root.get("relator"), relator));
            }

            // Se não há predicados, retorna todos os registros
            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
