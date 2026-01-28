package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.TipoVoto;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Voto;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ColegiadoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProfessorRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ReuniaoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.VotoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.specification.ProcessoSpecifications;

@Service
public class ReuniaoService {

    @Autowired
    private ProcessoRepository processoRepository;

    @Autowired
    private ColegiadoRepository colegiadoRepository;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Transactional
    public Reuniao criarReuniao(Reuniao reuniao, List<Long> processosIds, Long colegiadoId) {
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
                .orElseThrow(() -> new RuntimeException("Colegiado não encontrado"));

        reuniao.setColegiado(colegiado);
        reuniao.setStatus(StatusReuniao.PROGRAMADA);

        List<Processo> processosSelecionados = processoRepository.findAllById(processosIds);

        for (Processo processo : processosSelecionados) {
            processo.setStatus(StatusProcesso.EM_PAUTA);
            reuniao.getProcessos().add(processo);
        }

        return reuniaoRepository.save(reuniao);
    }

    @Transactional(readOnly = true)
    public List<Reuniao> listarReunioesPorMembro(Long professorId, StatusReuniao status) {
        return reuniaoRepository.findByMembroIdAndStatus(professorId, status);
    }

    @Transactional(readOnly = true)
    public List<Reuniao> listarTodas(StatusReuniao status) {
        if (status != null) {
            return reuniaoRepository.findAll().stream()
                    .filter(r -> r.getStatus() == status)
                    .toList();
        }
        return reuniaoRepository.findAll();
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
        // Se o status for nulo, busca todas as reuniões do colegiado sem filtrar por status
        if (status == null) {
            return reuniaoRepository.findByColegiadoId(colegiadoId);
        } else {
            return reuniaoRepository.findByColegiadoIdAndStatus(colegiadoId, status);
        }
    }

    @Transactional(readOnly = true)
    public List<Reuniao> listarTodasComFiltro(StatusReuniao status) {
        if (status == null) {
            return reuniaoRepository.findAll();
        }
        return reuniaoRepository.findAll().stream()
                .filter(r -> r.getStatus() == status)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Reuniao> listarReunioesDoProfessor(Long professorId, StatusReuniao status) {
        if (status == null) {
            return reuniaoRepository.findByColegiadoMembrosId(professorId);
        }
        return reuniaoRepository.findByColegiadoMembrosIdAndStatus(professorId, status);
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

    @Transactional(readOnly = true)
    public Reuniao findByIdComDetalhes(Long id) {
        return reuniaoRepository.findReuniaoComDetalhesById(id);
    }

    @Transactional
    public void registrarVotos(Long reuniaoId, Long processoId, Map<Long, String> votos) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reuniao nao encontrada");
        }

        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo nao encontrado"));

        // Remove votos anteriores deste processo nesta reuniao
        reuniao.getVotos().removeIf(v -> v.getProcesso().getId().equals(processoId));

        // Registra os novos votos
        for (Map.Entry<Long, String> entry : votos.entrySet()) {
            Long professorId = entry.getKey();
            String tipoVotoStr = entry.getValue();

            Professor professor = professorRepository.findById(professorId)
                    .orElseThrow(() -> new IllegalArgumentException("Professor nao encontrado: " + professorId));

            Voto voto;
            if ("AUSENTE".equals(tipoVotoStr)) {
                voto = new Voto(professor, processo, reuniao);
            } else {
                TipoVoto tipoVoto = TipoVoto.valueOf(tipoVotoStr);
                voto = new Voto(professor, tipoVoto, processo, reuniao);
            }

            reuniao.getVotos().add(voto);
        }

        // Atualiza status do processo para JULGADO se todos votaram
        int totalMembros = reuniao.getColegiado().getMembros().size();
        long votosRegistrados = reuniao.getVotos().stream()
                .filter(v -> v.getProcesso().getId().equals(processoId))
                .count();

        if (votosRegistrados == totalMembros) {
            processo.setStatus(StatusProcesso.JULGADO);
            processoRepository.save(processo);
        }

        reuniaoRepository.save(reuniao);
    }

    @Transactional
    public void retirarProcessoDaPauta(Long reuniaoId, Long processoId) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reuniao nao encontrada");
        }

        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo nao encontrado"));

        // Remove o processo da pauta
        reuniao.getProcessos().remove(processo);

        // Remove votos relacionados a este processo nesta reuniao
        reuniao.getVotos().removeIf(v -> v.getProcesso().getId().equals(processoId));

        // Volta o status do processo para DISTRIBUIDO
        processo.setStatus(StatusProcesso.DISTRIBUIDO);
        processoRepository.save(processo);

        reuniaoRepository.save(reuniao);
    }
}
