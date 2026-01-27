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
        // 1. Recupera o colegiado (supondo que venha da sessão/usuário logado no futuro)
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new RuntimeException("Colegiado não encontrado"));

        reuniao.setColegiado(colegiado);
        reuniao.setStatus(StatusReuniao.PROGRAMADA);

        // 2. Recupera os processos selecionados pelo ID
        if (processosIds != null && !processosIds.isEmpty()) {
            List<Processo> processosSelecionados = processoRepository.findAllById(processosIds);

            for (Processo processo : processosSelecionados) {
                // Regra de Negócio: Ao entrar na pauta, o status do processo muda
                processo.setStatus(StatusProcesso.EM_PAUTA);

                // Associa o processo à reunião (relação ManyToMany)
                reuniao.getProcessos().add(processo);

                // Opcional: Se o relacionamento for bidirecional estrito, adicione:
                processo.getReunioes().add(reuniao);
            }
        }

        // 3. Salva a reunião (o Cascade persistirá as associações se configurado, 
        // mas aqui salvamos a reunião que é a dona da relação na tabela de junção)
        return reuniaoRepository.save(reuniao);
    }

// CORREÇÃO: Recebe Long (do controller) e converte para Colegiado (para a Specification)
    public List<Processo> listarProcessosDisponiveisParaReuniao(Long colegiadoId) {

        // 1. Busca o Colegiado pelo ID
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new IllegalArgumentException("Colegiado não encontrado com id: " + colegiadoId));

        // 2. Chama a Specification passando o objeto Colegiado resolvido
        return processoRepository.findAll(
                ProcessoSpecifications.buildSpecificationForColegiado(
                        colegiado, // Objeto Colegiado obrigatório
                        StatusProcesso.DISTRIBUIDO, // Apenas processos distribuídos
                        null, // Ignora filtro de aluno
                        null // Ignora filtro de relator
                )
        );
    }

}
