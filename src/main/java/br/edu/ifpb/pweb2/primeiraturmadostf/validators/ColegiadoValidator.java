package br.edu.ifpb.pweb2.primeiraturmadostf.validators;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.edu.ifpb.pweb2.primeiraturmadostf.datatransferobject.ColegiadoDTO;

@Component
public class ColegiadoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return ColegiadoDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ColegiadoDTO dto = (ColegiadoDTO) target;
        
        // Validar data fim > data início (se ambas estiverem preenchidas)
        if (dto.getDataFim() != null && dto.getDataInicio() != null) {
            if (dto.getDataFim().isBefore(dto.getDataInicio()) || dto.getDataFim().isEqual(dto.getDataInicio())) {
                errors.rejectValue("dataFim", "dataFim.invalid", 
                    "Data de fim deve ser posterior à data de início");
            }
        }
        
        // Filtrar IDs nulos da lista de membros (caso o usuário não selecione todos os dropdowns)
        List<Long> membrosSelecionados = dto.getMembrosIds().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Validar Duplicatas DENTRO da lista de membros
        long distintos = membrosSelecionados.stream().distinct().count();
        if (membrosSelecionados.size() != distintos) {
            errors.rejectValue("membrosIds", "membrosIds.duplicate", 
                "Não selecione o mesmo professor mais de uma vez na lista de membros.");
        }

        // Validar se o COORDENADOR também foi selecionado como MEMBRO
        if (dto.getCoordenadorId() != null && membrosSelecionados.contains(dto.getCoordenadorId())) {
            errors.rejectValue("coordenadorId", "coordenador.duplicate", 
                "O Coordenador não pode ser selecionado também como membro comum.");
                
            // Esse fecho aqui marca erro na lista de membros também para ficar visualmente claro
            errors.rejectValue("membrosIds", "membrosIds.conflict", 
                "Remova o Coordenador da lista de membros.");
        }

        if (membrosSelecionados.size() < 3) {
             errors.rejectValue("membrosIds", "membrosIds.size", 
                "É necessário selecionar exatamente 3 membros professores.");
        }
                
            
        }
    }


