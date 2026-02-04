package br.edu.ifpb.pweb2.primeiraturmadostf.validators;

import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

import br.edu.ifpb.pweb2.primeiraturmadostf.anotations.Matricula;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MatriculaValidator implements ConstraintValidator<Matricula, String> {

    // Regex: formato estrutural
    private static final Pattern MATRICULA_PATTERN =
            Pattern.compile("^(19|20)\\d{2}[12]\\d{2}\\d{4}$");

    // Lista de cursos válidos (exemplo)
    private static final Set<String> CURSOS_VALIDOS = Set.of(
            "37", // Sistemas para Internet
            "45", // Engenharia de Softaware
            "12"  // Redes
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null) {
            return false;
        }

        // Validação de formato
        if (!MATRICULA_PATTERN.matcher(value).matches()) {
            return false;
        }

        // Extração das partes da matrícula
        int ano = Integer.parseInt(value.substring(0, 4));
        int periodo = Integer.parseInt(value.substring(4, 5));
        String codigoCurso = value.substring(5, 7);
        // String numeroAluno = value.substring(7, 11); // se precisar depois

        int anoAtual = LocalDate.now().getYear();
        int mesAtual = LocalDate.now().getMonthValue();

        // Regra: ano não pode ser futuro
        if (ano > anoAtual) {
            return false;
        }

        // Regra: período compatível com o calendário
        // 1º semestre: jan–jun → período 1
        // 2º semestre: jul–dez → período 2
        if (ano == anoAtual) {
            if (mesAtual <= 6 && periodo != 1) {
                return false;
            }
            if (mesAtual > 6 && periodo != 2) {
                return false;
            }
        }

        // Regra: curso deve existir
        if (!CURSOS_VALIDOS.contains(codigoCurso)) {
            return false;
        }

        return true;
    }
}
