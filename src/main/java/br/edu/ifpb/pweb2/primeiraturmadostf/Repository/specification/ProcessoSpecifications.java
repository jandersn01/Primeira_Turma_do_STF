package br.edu.ifpb.pweb2.primeiraturmadostf.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;

/**
 * Classe utilitária para construir Specifications dinâmicas para consultas de Processo.
 * Permite criar queries flexíveis combinando filtros opcionais.
 */
public class ProcessoSpecifications {
    
    /**
     * Filtra processos por aluno interessado.
     * Se o aluno for null, retorna null (não aplica filtro).
     */
    public static Specification<Processo> byInteressado(Aluno aluno) {
        return (root, query, cb) -> 
            aluno != null ? cb.equal(root.get("interessado"), aluno) : null;
    }
    
    /**
     * Filtra processos por status.
     * Se o status for null, retorna null (não aplica filtro).
     */
    public static Specification<Processo> byStatus(StatusProcesso status) {
        return (root, query, cb) -> 
            status != null ? cb.equal(root.get("status"), status) : null;
    }
    
    /**
     * Filtra processos por assunto.
     * Se o assunto for null, retorna null (não aplica filtro).
     */
    public static Specification<Processo> byAssunto(Assunto assunto) {
        return (root, query, cb) -> 
            assunto != null ? cb.equal(root.get("assunto"), assunto) : null;
    }
    
    /**
     * Filtra processos por relator (Professor).
     * Se o relator for null, retorna null (não aplica filtro).
     */
    public static Specification<Processo> byRelator(Professor relator) {
        return (root, query, cb) -> 
            relator != null ? cb.equal(root.get("relator"), relator) : null;
    }
    
    /**
     * Filtra processos que estão em reuniões de um colegiado específico.
     * Se o colegiado for null, retorna null (não aplica filtro).
     */
    public static Specification<Processo> byColegiado(Colegiado colegiado) {
        return (root, query, cb) -> {
            if (colegiado == null) {
                return null;
            }
            // Garantir que a query retorne resultados distintos para evitar duplicatas
            query.distinct(true);
            // Join com reunioes e depois com colegiado
            return cb.equal(root.join("reunioes").get("colegiado"), colegiado);
        };
    }
    
    /**
     * Constrói uma Specification combinando todos os filtros fornecidos.
     * Os filtros são combinados com AND.
     * 
     * @param aluno Aluno interessado (pode ser null)
     * @param status Status do processo (pode ser null)
     * @param assunto Assunto do processo (pode ser null)
     * @return Specification combinada com todos os filtros não-nulos
     */
    public static Specification<Processo> buildSpecification(
            Aluno aluno, StatusProcesso status, Assunto assunto) {
        return Specification
            .where(byInteressado(aluno))
            .and(byStatus(status))
            .and(byAssunto(assunto));
    }
    
    /**
     * Constrói uma Specification combinando todos os filtros fornecidos para processos do colegiado.
     * Os filtros são combinados com AND.
     * 
     * @param colegiado Colegiado (obrigatório)
     * @param status Status do processo (pode ser null)
     * @param aluno Aluno interessado (pode ser null)
     * @param relator Professor relator (pode ser null)
     * @return Specification combinada com todos os filtros não-nulos
     */
    public static Specification<Processo> buildSpecificationForColegiado(
            Colegiado colegiado, StatusProcesso status, Aluno aluno, Professor relator) {
        return Specification
            .where(byColegiado(colegiado))
            .and(byStatus(status))
            .and(byInteressado(aluno))
            .and(byRelator(relator));
    }
}

