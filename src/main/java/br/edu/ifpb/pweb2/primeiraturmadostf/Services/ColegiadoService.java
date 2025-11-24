package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject.ColegiadoDTO;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Colegiado;
import br.edu.ifpb.pweb2.primeiraturmadostf.model.Professor;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ColegiadoRepository;
import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ProfessorRepository;

@Service
@Transactional
public class ColegiadoService {

    private final ProfessorService professorService;

    private final ColegiadoRepository colegiadoRepository;


    @Autowired
    public ColegiadoService(ColegiadoRepository repository, ProfessorService professorService){
        this.colegiadoRepository = repository;
        this.professorService = professorService ;
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

        if(dto.getId() != null){
            colegiado = this.findById(dto.getId());
           // .orElse();
        
        } else {
            colegiado = new Colegiado();
        }

        colegiado.setCurso(dto.getCurso());
        colegiado.setPortaria(dto.getPortaria());
        colegiado.setDataInicio(dto.getDataInicio());
        colegiado.setDataFim(dto.getDataFim());

        if (dto.getMembrosIds() != null && !dto.getMembrosIds().isEmpty()){
            colegiado.setMembros(new HashSet<>());

            List<Professor> professoresSelecionados = professorService.findAllById(dto.getMembrosIds());
            colegiado.getMembros().addAll(professoresSelecionados);

            professoresSelecionados.forEach(prof -> prof.getColegiados().add(colegiado));
        }
        return colegiadoRepository.save(colegiado);
        
    }

    public boolean remove(Long id) {
        Colegiado assunto = this.findById(id);
        this.colegiadoRepository.delete(assunto);
        return true;
    }

    public ColegiadoDTO getForEdit(Long id){
        Colegiado colegiado = this.findById(id);

        ColegiadoDTO dto = new ColegiadoDTO();
        dto.setId(colegiado.getId());
        dto.setCurso(colegiado.getCurso());
        dto.setPortaria(colegiado.getPortaria());
        dto.setDataInicio(colegiado.getDataInicio());
        dto.setDataFim(colegiado.getDataFim());

        List<Long> ids = colegiado.getMembros().stream()
        .map(Professor::getId).collect(Collectors.toList());

        dto.setMembrosIds(ids);

        while (dto.getMembrosIds().size() < 5) {
            dto.getMembrosIds().add(null);
        }

        return dto;

    }

}
