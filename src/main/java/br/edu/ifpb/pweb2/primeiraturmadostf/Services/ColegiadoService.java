package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject.ColegiadoDTO;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Aluno;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Curso;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ColegiadoRepository;


@Service
@Transactional
public class ColegiadoService {

    private final ProfessorService professorService;
    private final ColegiadoRepository colegiadoRepository;
    private final AlunoService alunoService;
    private final CursoService cursoService;


    @Autowired
    public ColegiadoService(ColegiadoRepository repository, ProfessorService professorService, AlunoService alunoService, CursoService cursoService){
        this.colegiadoRepository = repository;
        this.professorService = professorService ;
        this.alunoService = alunoService;
        this.cursoService = cursoService;
    }

    public List<Colegiado> findAll() {
        return this.colegiadoRepository.findAll();
    }

  public Colegiado findById(Long id) {
        return this.colegiadoRepository.findById(id)
                .orElse(null);
    }


    public Colegiado save(ColegiadoDTO dto) {
        Colegiado colegiado;

        if (dto.getId() != null) {
            colegiado = this.findById(dto.getId());
            if (colegiado.getMembros() != null) {
                colegiado.getMembros().clear();
            }
        } else {
            colegiado = new Colegiado();
            colegiado.setMembros(new HashSet<>());
        }

        colegiado.setPortaria(dto.getPortaria());
        colegiado.setDataInicio(dto.getDataInicio());
        colegiado.setDataFim(dto.getDataFim());
        
        if (dto.getCursoId() != null) {
            Curso curso = cursoService.findById(dto.getCursoId());
            colegiado.setCurso(curso);
        }

        if (dto.getCoordenadorId() != null) {
            Professor coordenador = professorService.findById(dto.getCoordenadorId());
            if (coordenador != null) {
                coordenador.setCoordenador(true); 
                professorService.save(coordenador);
                colegiado.getMembros().add(coordenador);
            }
        }

        if (dto.getMembrosIds() != null) {
            for (Long profId : dto.getMembrosIds()) {
                if (profId != null) {
                    Professor membro = professorService.findById(profId);
                    if (membro != null) {
                        colegiado.getMembros().add(membro);
                    }
                }
            }
        }
        
        if (dto.getAlunoId() != null) {
            Aluno aluno = alunoService.findById(dto.getAlunoId());
            colegiado.setRepresentante(aluno);
        }

        return colegiadoRepository.save(colegiado);
        
    }

    public boolean remove(Long id) {
        Colegiado colegiado = this.findById(id); 
        if (colegiado != null) {
            this.colegiadoRepository.delete(colegiado);
            return true;
        }
        return false;
    }

    public ColegiadoDTO getForEdit(Long id){
        Colegiado colegiado = this.findById(id);
        if (colegiado == null) return null;

        ColegiadoDTO dto = new ColegiadoDTO();
        dto.setId(colegiado.getId());
        
        if (colegiado.getCurso() != null) {
            dto.setCursoId(colegiado.getCurso().getId());
        }
        dto.setPortaria(colegiado.getPortaria());
        dto.setDataInicio(colegiado.getDataInicio());
        dto.setDataFim(colegiado.getDataFim());

        if(colegiado.getRepresentante() != null) {
            dto.setAlunoId(colegiado.getRepresentante().getId());
        }

        for (Professor p : colegiado.getMembros()) {
            if (Boolean.TRUE.equals(p.getCoordenador())) {
                dto.setCoordenadorId(p.getId());
            } else {
                if (dto.getMembrosIds().contains(null)) {
                    int index = dto.getMembrosIds().indexOf(null);
                    dto.getMembrosIds().set(index, p.getId());
                } else if (dto.getMembrosIds().size() < 3) {
                    dto.getMembrosIds().add(p.getId());
                }
            }
        }

        while (dto.getMembrosIds().size() < 3) {
            dto.getMembrosIds().add(null);
        }
        
        return dto;

    }

    public Colegiado findAtivoPorCurso(Curso curso) {
    return colegiadoRepository.findByCursoAndDataFimIsNull(curso);
}

}
