package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProcessoRepository;

@Service
@Transactional
public class ProcessoService {

    private final ProcessoRepository processoRepository;

    public ProcessoService(ProcessoRepository processoRepository) {
        this.processoRepository = processoRepository;
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

}

