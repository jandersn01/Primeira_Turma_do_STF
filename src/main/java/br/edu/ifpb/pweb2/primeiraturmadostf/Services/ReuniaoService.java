package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ColegiadoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ReuniaoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.specification.ProcessoSpecifications;

@Service
public class ReuniaoService {

    @Autowired
    private ProcessoRepository processoRepository;

    @Autowired
    private ColegiadoRepository colegiadoRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Transactional
    public Reuniao criarReuniao(Reuniao reuniao, List<Long> processosIds, Long colegiadoId) {
        // 1. Recupera o colegiado
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new RuntimeException("Colegiado não encontrado"));

        reuniao.setColegiado(colegiado);
        reuniao.setStatus(StatusReuniao.PROGRAMADA);

        // 2. Recupera os processos selecionados pelo ID e atualiza status
        if (processosIds != null && !processosIds.isEmpty()) {
            List<Processo> processosSelecionados = processoRepository.findAllById(processosIds);

            for (Processo processo : processosSelecionados) {
                processo.setStatus(StatusProcesso.EM_PAUTA);
                reuniao.getProcessos().add(processo);
                // processo.getReunioes().add(reuniao); // Opcional bidirecional
            }
        }

        // 3. Salva a reunião
        return reuniaoRepository.save(reuniao);
    }

    @Transactional(readOnly = true)
    public List<Processo> listarProcessosDisponiveisParaReuniao(Long colegiadoId) {
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new IllegalArgumentException("Colegiado não encontrado com id: " + colegiadoId));

        return processoRepository.findAll(
                ProcessoSpecifications.buildSpecificationForColegiado(
                        colegiado, 
                        StatusProcesso.DISTRIBUIDO, 
                        null, 
                        null
                )
        );
    }

    // --- NOVO MÉTODO PARA A LISTAGEM ---
    @Transactional(readOnly = true)
    public List<Reuniao> listarReunioesDoColegiado(Long colegiadoId, StatusReuniao status) {
        if (status != null) {
            // Se o usuário selecionou um filtro (ex: PROGRAMADA), busca filtrado
            return reuniaoRepository.findByColegiadoIdAndStatus(colegiadoId, status);
        } else {
            // Se não tem filtro, traz todas as reuniões do colegiado
            return reuniaoRepository.findByColegiadoId(colegiadoId);
        }
    }

    public boolean remove(Long id) {
        return reuniaoRepository.removeById(id);
    }
 

    public Reuniao findById(Long id) {
        return reuniaoRepository.findReuniaoById(id);
    }
}