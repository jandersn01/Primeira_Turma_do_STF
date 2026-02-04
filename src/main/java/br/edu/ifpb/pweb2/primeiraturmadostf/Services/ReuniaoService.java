package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // IMPORT CORRETO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Processo;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Reuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusProcesso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.StatusReuniao;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.TipoDecisao;
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

        // Valida que todos os processos têm parecer do relator
        for (Processo processo : processosSelecionados) {
            if (processo.getDecisaoRelator() == null) {
                throw new IllegalStateException("O processo " + processo.getNumero() +
                    " não possui parecer do relator. Apenas processos com parecer podem ser incluídos na pauta.");
            }
            if (processo.getStatus() != StatusProcesso.DISPONIVEL) {
                throw new IllegalStateException("O processo " + processo.getNumero() +
                    " não está disponível para pauta. Status atual: " + processo.getStatus().getDescricao());
            }
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

        // Busca processos no status DISPONIVEL (relator já emitiu parecer)
        return processoRepository.findAll(
                ProcessoSpecifications.buildSpecificationForColegiado(
                        colegiado,
                        StatusProcesso.DISPONIVEL,
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

    public Page<Reuniao> listarReunioesPorMembroPaginado(Long professorId, StatusReuniao status, Pageable pageable) {
        return reuniaoRepository.findByMembroIdAndStatus(professorId, status, pageable);
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

        // Volta o status do processo para DISPONIVEL (já tem parecer do relator)
        processo.setStatus(StatusProcesso.DISPONIVEL);
        processoRepository.save(processo);

        reuniaoRepository.save(reuniao);
    }

    @Transactional(readOnly = true)
    public Page<Reuniao> listarTodasPaginadas(StatusReuniao status, Pageable pageable) {
        if (status == null) {
            return reuniaoRepository.findAll(pageable);
        }
        // Utiliza o método que já existe no seu repositório mas com Pageable
        return reuniaoRepository.findAllComFiltro(status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Reuniao> listarReunioesDoProfessorPaginadas(Long professorId, StatusReuniao status, Pageable pageable) {
        // Utiliza a query que já existe no seu repositório ajustada para paginação
        return reuniaoRepository.findByMembroIdAndStatus(professorId, status, pageable);
    }

    /**
     * Apregoa um processo para julgamento.
     * Muda o status do processo de EM_PAUTA para EM_JULGAMENTO.
     *
     * @param reuniaoId ID da reunião
     * @param processoId ID do processo a ser apregoado
     * @throws IllegalArgumentException se reunião ou processo não encontrados
     * @throws IllegalStateException se reunião não está EM_ANDAMENTO,
     *         processo não está EM_PAUTA ou já existe outro processo em julgamento
     */
    @Transactional
    public void apregoarProcesso(Long reuniaoId, Long processoId) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reunião não encontrada");
        }

        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            throw new IllegalStateException("A reunião não está em andamento");
        }

        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo não encontrado"));

        if (!reuniao.getProcessos().contains(processo)) {
            throw new IllegalStateException("Processo não pertence à pauta desta reunião");
        }

        if (processo.getStatus() != StatusProcesso.EM_PAUTA) {
            throw new IllegalStateException("Apenas processos com status EM_PAUTA podem ser apregoados. Status atual: " + processo.getStatus().getDescricao());
        }

        // Verifica se já existe outro processo em julgamento nesta reunião
        boolean existeOutroEmJulgamento = reuniao.getProcessos().stream()
                .anyMatch(p -> p.getStatus() == StatusProcesso.EM_JULGAMENTO && !p.getId().equals(processoId));

        if (existeOutroEmJulgamento) {
            throw new IllegalStateException("Já existe outro processo sendo julgado. Conclua o julgamento atual antes de apregoar outro processo.");
        }

        processo.setStatus(StatusProcesso.EM_JULGAMENTO);
        processoRepository.save(processo);
    }

    /**
     * Conclui o julgamento de um processo, calculando o resultado automaticamente.
     *
     * Lógica de cálculo:
     * - Conta votos COM_RELATOR vs DIVERGENTE (ignora AUSENTE)
     * - Se votos COM_RELATOR >= votos DIVERGENTE → resultado = parecer do relator
     * - Caso contrário → resultado = inverso do parecer do relator
     *
     * @param reuniaoId ID da reunião
     * @param processoId ID do processo
     * @param votos Mapa de votos (professorId -> tipoVoto)
     * @return Resultado do julgamento (DEFERIMENTO ou INDEFERIMENTO)
     * @throws IllegalArgumentException se reunião ou processo não encontrados
     * @throws IllegalStateException se processo não está EM_JULGAMENTO ou não tem parecer do relator
     */
    @Transactional
    public br.edu.ifpb.pweb2.primeiraturmadostf.model.TipoDecisao concluirJulgamento(Long reuniaoId, Long processoId, Map<Long, String> votos) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reunião não encontrada");
        }

        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            throw new IllegalStateException("A reunião não está em andamento");
        }

        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo não encontrado"));

        if (processo.getStatus() != StatusProcesso.EM_JULGAMENTO) {
            throw new IllegalStateException("Processo não está em julgamento. Status atual: " + processo.getStatus().getDescricao());
        }

        if (processo.getDecisaoRelator() == null) {
            throw new IllegalStateException("O processo não possui parecer do relator. Não é possível concluir o julgamento.");
        }

        // Remove votos anteriores deste processo nesta reunião
        reuniao.getVotos().removeIf(v -> v.getProcesso().getId().equals(processoId));

        // Registra os novos votos e conta
        int votosComRelator = 0;
        int votosDivergentes = 0;

        for (Map.Entry<Long, String> entry : votos.entrySet()) {
            Long professorId = entry.getKey();
            String tipoVotoStr = entry.getValue();

            Professor professor = professorRepository.findById(professorId)
                    .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado: " + professorId));

            Voto voto;
            if ("AUSENTE".equals(tipoVotoStr)) {
                voto = new Voto(professor, processo, reuniao);
                // Ausente não conta para o cálculo
            } else {
                TipoVoto tipoVoto = TipoVoto.valueOf(tipoVotoStr);
                voto = new Voto(professor, tipoVoto, processo, reuniao);

                if (tipoVoto == TipoVoto.COM_RELATOR) {
                    votosComRelator++;
                } else if (tipoVoto == TipoVoto.DIVERGENTE) {
                    votosDivergentes++;
                }
            }

            reuniao.getVotos().add(voto);
        }

        // Calcula o resultado: maioria simples
        br.edu.ifpb.pweb2.primeiraturmadostf.model.TipoDecisao resultado;
        if (votosComRelator >= votosDivergentes) {
            // Maioria votou com o relator → resultado = parecer do relator
            resultado = processo.getDecisaoRelator();
        } else {
            // Maioria votou divergente → resultado = inverso do parecer
            resultado = processo.getDecisaoRelator().inverter();
        }

        // Atualiza o processo
        processo.setResultadoJulgamento(resultado);
        processo.setDataJulgamento(LocalDate.now());
        processo.setStatus(StatusProcesso.JULGADO);
        processoRepository.save(processo);

        reuniaoRepository.save(reuniao);

        return resultado;
    }

    /**
     * Verifica se existe um processo em julgamento na reunião.
     */
    @Transactional(readOnly = true)
    public Processo getProcessoEmJulgamento(Long reuniaoId) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            return null;
        }

        return reuniao.getProcessos().stream()
                .filter(p -> p.getStatus() == StatusProcesso.EM_JULGAMENTO)
                .findFirst()
                .orElse(null);
    }

    /**
     * Registra o voto individual de um professor em um processo.
     * O professor vota COM_RELATOR ou DIVERGENTE em relação ao parecer do relator.
     * A decisão final (DEFERIMENTO/INDEFERIMENTO) é calculada automaticamente.
     *
     * @param reuniaoId ID da reunião
     * @param processoId ID do processo
     * @param professorId ID do professor que está votando
     * @param tipoVotoStr Tipo do voto (COM_RELATOR ou DIVERGENTE)
     * @param justificativa Justificativa opcional (se preenchida, mínimo 10 caracteres)
     * @throws IllegalArgumentException se reunião, processo ou professor não encontrados
     * @throws IllegalStateException se reunião não está EM_ANDAMENTO, processo não está em pauta,
     *         professor não é membro do colegiado, professor já votou, ou relator não deu parecer
     */
    @Transactional
    public Voto registrarVotoIndividual(Long reuniaoId, Long processoId, Long professorId,
                                         String tipoVotoStr, String justificativa) {
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reunião não encontrada");
        }

        if (reuniao.getStatus() != StatusReuniao.EM_ANDAMENTO) {
            throw new IllegalStateException("A reunião não está em andamento");
        }

        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new IllegalArgumentException("Processo não encontrado"));

        // Verifica se processo pertence à pauta (por ID)
        boolean pertencePauta = reuniao.getProcessos().stream()
                .anyMatch(p -> p.getId().equals(processoId));
        if (!pertencePauta) {
            throw new IllegalStateException("Processo não pertence à pauta desta reunião");
        }

        // Verifica se processo está na pauta (EM_PAUTA ou EM_JULGAMENTO)
        if (processo.getStatus() != StatusProcesso.EM_PAUTA &&
            processo.getStatus() != StatusProcesso.EM_JULGAMENTO) {
            throw new IllegalStateException("Processo não está disponível para votação. Status atual: " + processo.getStatus().getDescricao());
        }

        // VALIDAÇÃO CRÍTICA: Verifica se o relator já deu parecer
        if (processo.getDecisaoRelator() == null) {
            throw new IllegalStateException("O relator ainda não emitiu parecer para este processo. Aguarde o parecer do relator para votar.");
        }

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new IllegalArgumentException("Professor não encontrado"));

        // Verifica se professor é membro do colegiado
        boolean membroColegiado = reuniao.getColegiado().getMembros().stream()
                .anyMatch(m -> m.getId().equals(professorId));
        if (!membroColegiado) {
            throw new IllegalStateException("Professor não é membro do colegiado desta reunião");
        }

        // Verifica se professor já votou neste processo nesta reunião
        if (votoRepository.existsByProfessorAndProcessoAndReuniao(professor, processo, reuniao)) {
            throw new IllegalStateException("Você já registrou seu voto neste processo");
        }

        // Valida justificativa (opcional, mas se preenchida, mínimo 10 caracteres)
        if (justificativa != null && !justificativa.trim().isEmpty() && justificativa.trim().length() < 10) {
            throw new IllegalArgumentException("A justificativa deve ter no mínimo 10 caracteres");
        }

        // Converte o tipo de voto (COM_RELATOR ou DIVERGENTE)
        TipoVoto tipoVoto = TipoVoto.valueOf(tipoVotoStr);

        // Calcula a decisão baseada no parecer do relator e no tipo de voto
        TipoDecisao decisao;
        if (tipoVoto == TipoVoto.COM_RELATOR) {
            // Se vota com o relator, a decisão é igual à do relator
            decisao = processo.getDecisaoRelator();
        } else {
            // Se vota divergente, a decisão é o inverso da do relator
            decisao = processo.getDecisaoRelator().inverter();
        }

        // Cria o voto
        Voto voto = new Voto();
        voto.setProfessor(professor);
        voto.setProcesso(processo);
        voto.setReuniao(reuniao);
        voto.setTipo(tipoVoto);
        voto.setDecisao(decisao);
        voto.setAusente(false);
        voto.setDataVoto(java.time.LocalDateTime.now());

        // Define justificativa se fornecida
        if (justificativa != null && !justificativa.trim().isEmpty()) {
            voto.setJustificativa(justificativa.trim());
        }

        reuniao.getVotos().add(voto);
        reuniaoRepository.save(reuniao);

        return voto;
    }

    /**
     * Busca o voto de um professor em um processo específico de uma reunião.
     */
    @Transactional(readOnly = true)
    public Voto getVotoDoProfessor(Long reuniaoId, Long processoId, Long professorId) {
        return votoRepository.findByIds(professorId, processoId, reuniaoId).orElse(null);
    }

    /**
     * Verifica se um professor já votou em um processo.
     */
    @Transactional(readOnly = true)
    public boolean professorJaVotou(Long reuniaoId, Long processoId, Long professorId) {
        Reuniao reuniao = findById(reuniaoId);
        Processo processo = processoRepository.findById(processoId).orElse(null);
        Professor professor = professorRepository.findById(professorId).orElse(null);

        if (reuniao == null || processo == null || professor == null) {
            return false;
        }

        return votoRepository.existsByProfessorAndProcessoAndReuniao(professor, processo, reuniao);
    }

    /**
     * Busca todos os votos de um professor em uma reunião.
     */
    @Transactional(readOnly = true)
    public List<Voto> getVotosDoProfessorNaReuniao(Long reuniaoId, Long professorId) {
        Reuniao reuniao = findById(reuniaoId);
        Professor professor = professorRepository.findById(professorId).orElse(null);

        if (reuniao == null || professor == null) {
            return java.util.Collections.emptyList();
        }

        return votoRepository.findByProfessorAndReuniao(professor, reuniao);
    }
}
