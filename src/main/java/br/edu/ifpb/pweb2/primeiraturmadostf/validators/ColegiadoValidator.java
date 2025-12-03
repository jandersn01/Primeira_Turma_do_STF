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
        
        // Validar máximo 5 membros e sem duplicatas
        if (dto.getMembrosIds() != null) {
            List<Long> membrosIds = dto.getMembrosIds().stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
                
            if (membrosIds.size() > 5) {
                errors.rejectValue("membrosIds", "membrosIds.max", 
                    "Selecione no máximo 5 membros diferentes");
            }
        }
    }
}

