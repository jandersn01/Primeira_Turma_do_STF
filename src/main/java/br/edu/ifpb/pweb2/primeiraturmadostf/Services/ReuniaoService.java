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
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProcessoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ReuniaoRepository;

@Service
@Transactional
public class ReuniaoService {

    private final ReuniaoRepository reuniaoRepository;
    private final ProcessoRepository processoRepository;

    @Autowired
    public ReuniaoService(ReuniaoRepository reuniaoRepository, ProcessoRepository processoRepository) {
        this.reuniaoRepository = reuniaoRepository;
        this.processoRepository = processoRepository;
    }

    public List<Reuniao> findAll() {
        return reuniaoRepository.findAll();
    }

    public Reuniao findById(Long id) {
        return reuniaoRepository.findById(id).orElse(null);
    }

    public List<Reuniao> findByColegiado(Colegiado colegiado) {
        return reuniaoRepository.findByColegiadoOrderByDataReuniaoDesc(colegiado);
    }

    public List<Reuniao> findByColegiadoAndStatus(Colegiado colegiado, StatusReuniao status) {
        return reuniaoRepository.findByColegiadoAndStatus(colegiado, status);
    }

    public Reuniao save(Reuniao reuniao) {
        return reuniaoRepository.save(reuniao);
    }

    public boolean existsSessaoEmAndamento() {
        return reuniaoRepository.existsByStatus(StatusReuniao.EM_ANDAMENTO);
    }

    public List<Reuniao> findSessoesEmAndamento() {
        return reuniaoRepository.findAllByStatus(StatusReuniao.EM_ANDAMENTO);
    }

    /**
     * Inicia uma sessao/reuniao previamente agendada.
     *
     * Regras de negocio:
     * - Apenas UMA sessao pode estar EM_ANDAMENTO por vez
     * - Sessao so pode ser iniciada se status = PROGRAMADA
     * - Apos iniciar, processos da pauta mudam status para EM_PAUTA
     *
     * @param reuniaoId ID da reuniao a ser iniciada
     * @param usuario Matricula do usuario que esta iniciando (para auditoria)
     * @return Reuniao iniciada
     * @throws IllegalStateException se ja houver sessao em andamento ou reuniao nao estiver PROGRAMADA
     */
    public Reuniao iniciarSessao(Long reuniaoId, String usuario) {
        // Busca a reuniao
        Reuniao reuniao = findById(reuniaoId);
        if (reuniao == null) {
            throw new IllegalArgumentException("Reuniao nao encontrada");
        }

        // Verifica se a reuniao esta PROGRAMADA
        if (reuniao.getStatus() != StatusReuniao.PROGRAMADA) {
            throw new IllegalStateException("Apenas reunioes com status PROGRAMADA podem ser iniciadas. Status atual: "
                + reuniao.getStatus().getDescricao());
        }

        // Verifica se ja existe sessao em andamento
        if (existsSessaoEmAndamento()) {
            List<Reuniao> sessoesEmAndamento = findSessoesEmAndamento();
            String mensagem = "Ja existe uma sessao em andamento. ";
            if (!sessoesEmAndamento.isEmpty()) {
                Reuniao sessaoAtiva = sessoesEmAndamento.get(0);
                mensagem += "Reuniao ID: " + sessaoAtiva.getId() + " do colegiado "
                    + sessaoAtiva.getColegiado().getCurso().getNome();
            }
            throw new IllegalStateException(mensagem);
        }

        // Altera status da reuniao para EM_ANDAMENTO
        reuniao.setStatus(StatusReuniao.EM_ANDAMENTO);
        reuniao.setDataInicioSessao(LocalDateTime.now());
        reuniao.setUsuarioInicioSessao(usuario);
        reuniaoRepository.save(reuniao);

        // Altera status dos processos da pauta para EM_PAUTA
        for (Processo processo : reuniao.getProcessos()) {
            if (processo.getStatus() == StatusProcesso.DISTRIBUIDO) {
                processo.setStatus(StatusProcesso.EM_PAUTA);
                processoRepository.save(processo);
            }
        }

        return reuniao;
    }

    /**
     * Encerra uma sessao em andamento.
     *
     * @param reuniaoId ID da reuniao a ser encerrada
     * @param usuario Matricula do usuario que esta encerrando (para auditoria)
     * @return Reuniao encerrada
     */
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
        reuniaoRepository.save(reuniao);

        return reuniao;
    }

    public boolean remove(Long id) {
        Reuniao reuniao = findById(id);
        if (reuniao != null) {
            reuniaoRepository.delete(reuniao);
            return true;
        }
        return false;
    }
}
