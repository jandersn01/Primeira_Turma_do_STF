package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.time.LocalDateTime;
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
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new RuntimeException("Colegiado não encontrado"));

        reuniao.setColegiado(colegiado);
        reuniao.setStatus(StatusReuniao.PROGRAMADA);

        if (processosIds != null && !processosIds.isEmpty()) {
            List<Processo> processosSelecionados = processoRepository.findAllById(processosIds);

            for (Processo processo : processosSelecionados) {
                processo.setStatus(StatusProcesso.EM_PAUTA);
                reuniao.getProcessos().add(processo);
            }
        }

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

    @Transactional(readOnly = true)
    public List<Reuniao> listarReunioesDoColegiado(Long colegiadoId, StatusReuniao status) {
        if (status != null) {
            return reuniaoRepository.findByColegiadoIdAndStatus(colegiadoId, status);
        } else {
            return reuniaoRepository.findByColegiadoId(colegiadoId);
        }
    }

    @Transactional(readOnly = true)
    public List<Reuniao> listarReunioesDoProfessor(Long professorId, StatusReuniao status) {
        if (status != null) {
            return reuniaoRepository.findByColegiadoMembrosIdAndStatus(professorId, status);
        } else {
            return reuniaoRepository.findByColegiadoMembrosId(professorId);
        }
    }

    public boolean existsSessaoEmAndamento() {
        return reuniaoRepository.existsByStatus(StatusReuniao.EM_ANDAMENTO);
    }

    @Transactional
    public Reuniao iniciarSessao(Long reuniaoId, String usuario) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reuniao nao encontrada");
        }

        if (reuniao.getStatus() != StatusReuniao.PROGRAMADA) {
            throw new IllegalStateException("Apenas reunioes com status PROGRAMADA podem ser iniciadas. Status atual: "
                + reuniao.getStatus().getDescricao());
        }

        if (existsSessaoEmAndamento()) {
            throw new IllegalStateException("Ja existe uma sessao em andamento.");
        }

        reuniao.setStatus(StatusReuniao.EM_ANDAMENTO);
        reuniao.setDataInicioSessao(LocalDateTime.now());
        reuniao.setUsuarioInicioSessao(usuario);

        return reuniaoRepository.save(reuniao);
    }

    @Transactional
    public Reuniao encerrarSessao(Long reuniaoId, String usuario) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reuniao nao encontrada");
        }

        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas reunioes EM_ANDAMENTO podem ser encerradas");
        }

        reuniao.setStatus(StatusReuniao.ENCERRADA);
        reuniao.setDataEncerramentoSessao(LocalDateTime.now());
        reuniao.setUsuarioEncerramentoSessao(usuario);

        return reuniaoRepository.save(reuniao);
    }

    public boolean remove(Long id) {
        return reuniaoRepository.removeById(id);
    }

    public Reuniao findById(Long id) {
        return reuniaoRepository.findReuniaoById(id);
    }
}
