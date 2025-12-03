package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Assunto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.specification.ProcessoSpecifications;

@Service
@Transactional
public class ProcessoService {

    private final ProcessoRepository processoRepository;

    private final ProfessorService professorService;

    @Autowired
    public ProcessoService(ProcessoRepository processoRepository, ProfessorService professorService) {
        this.processoRepository = processoRepository;
        this.professorService = professorService;
    }

    public List<Processo> findAll() {
        return this.processoRepository.findAll();
    }

    public Processo findById(Long id) {
        return this.processoRepository.findById(id)
                .orElse(null);
    }

    public List<Processo> findByRelator(Professor relator) {
        return this.processoRepository.findByRelator(relator);
    }

    public List<Processo> findByRelatorAndStatus(Professor relator, StatusProcesso status) {
        return this.processoRepository.findByRelatorAndStatus(relator, status);
    }

    public Processo save(Processo processo) {
        // Se o processo não tem número, gerar automaticamente
        if (processo.getNumero() == null || processo.getNumero().isEmpty()) {
            processo.setNumero(gerarNumeroProcesso());
        }
        
        // Se não tem data de recepção, definir como hoje
        if (processo.getDataRecepcao() == null) {
            processo.setDataRecepcao(LocalDate.now());
        }
        
        // Se não tem status, definir como CRIADO
        if (processo.getStatus() == null) {
            processo.setStatus(StatusProcesso.CRIADO);
        }
        
        return processoRepository.save(processo);
    }
    
    /**
     * Gera um número de processo no formato: ANO/SEQUENCIAL
     * Exemplo: 2024/001, 2024/002, etc.
     */
    private String gerarNumeroProcesso() {
        int anoAtual = LocalDate.now().getYear();
        String prefixo = String.valueOf(anoAtual);
        
        // Buscar o último processo do ano atual
        List<Processo> processosDoAno = processoRepository.findAll().stream()
                .filter(p -> p.getNumero() != null && p.getNumero().startsWith(prefixo + "/"))
                .toList();
        
        // Encontrar o maior sequencial
        int maiorSequencial = 0;
        for (Processo p : processosDoAno) {
            try {
                String[] partes = p.getNumero().split("/");
                if (partes.length == 2 && partes[0].equals(prefixo)) {
                    int sequencial = Integer.parseInt(partes[1]);
                    if (sequencial > maiorSequencial) {
                        maiorSequencial = sequencial;
                    }
                }
            } catch (NumberFormatException e) {
                // Ignorar números com formato inválido
            }
        }
        
        // Incrementar e formatar
        int novoSequencial = maiorSequencial + 1;
        return String.format("%s/%03d", prefixo, novoSequencial);
    }

    public boolean remove(Long id) {
        Processo processo = this.findById(id);
        if (processo != null) {
            this.processoRepository.delete(processo);
            return true;
        }
        return false;
    }

    /**
     * Busca processos de um aluno com filtros opcionais e ordenação.
     * 
     * @param aluno Aluno interessado (obrigatório)
     * @param status Status do processo (opcional, null para todos)
     * @param assunto Assunto do processo (opcional, null para todos)
     * @param ordenacao "asc" para crescente, "desc" para decrescente, null para crescente (padrão)
     * @return Lista de processos filtrados e ordenados
     */
    public List<Processo> findByInteressadoWithFilters(
            Aluno aluno, StatusProcesso status, Assunto assunto, String ordenacao) {
        
        // Construir specification com filtros dinâmicos
        Specification<Processo> spec = ProcessoSpecifications.buildSpecification(
            aluno, status, assunto);
        
        // Definir ordenação (padrão: crescente por data de recepção)
        Sort sort = (ordenacao != null && ordenacao.equalsIgnoreCase("desc")) 
            ? Sort.by("dataRecepcao").descending()
            : Sort.by("dataRecepcao").ascending();
        
        return processoRepository.findAll(spec, sort);
    }
    
    /**
     * Busca processos de um colegiado com filtros opcionais e ordenação.
     * Os processos são encontrados através das reuniões do colegiado.
     * 
     * @param colegiado Colegiado (obrigatório)
     * @param status Status do processo (opcional, null para todos)
     * @param aluno Aluno interessado (opcional, null para todos)
     * @param relator Professor relator (opcional, null para todos)
     * @param ordenacao "asc" para crescente, "desc" para decrescente, null para crescente (padrão)
     * @return Lista de processos filtrados e ordenados
     */
    public List<Processo> findByColegiadoWithFilters(
            Colegiado colegiado, StatusProcesso status, Aluno aluno, 
            Professor relator, String ordenacao) {
        
        // Construir specification com filtros dinâmicos
        Specification<Processo> spec = ProcessoSpecifications.buildSpecificationForColegiado(
            colegiado, status, aluno, relator);
        
        // Definir ordenação (padrão: crescente por data de recepção)
        Sort sort = (ordenacao != null && ordenacao.equalsIgnoreCase("desc")) 
            ? Sort.by("dataRecepcao").descending()
            : Sort.by("dataRecepcao").ascending();
        
        return processoRepository.findAll(spec, sort);
    }

    public void distribuirProcesso(Long processoId, Long relatorId) {
    Processo processo = findById(processoId);
    
    if (processo == null) {
        throw new IllegalArgumentException("Processo não encontrado");
    }

    if (processo.getStatus() != StatusProcesso.CRIADO) {
        throw new IllegalStateException("O processo não está no estado CRIADO e não pode ser distribuído.");
    }

    Professor relator = professorService.findById(relatorId);
    if (relator == null) {
        throw new IllegalArgumentException("Professor relator não encontrado");
    }

    // Atualização dos campos
    processo.setRelator(relator);
    processo.setStatus(StatusProcesso.DISTRIBUIDO);
    processo.setDataDistribuicao(LocalDate.now());

    processoRepository.save(processo);
}

}

