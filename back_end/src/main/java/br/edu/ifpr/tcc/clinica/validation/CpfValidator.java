package br.edu.ifpr.tcc.clinica.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; // Obrigatoriedade fica com @NotBlank.
        String cpf = value.replaceAll("\\D", "");
        if (!cpf.matches("[0-9]{11}") || cpf.chars().distinct().count() == 1) return false;
        int first = digit(cpf.substring(0, 9), 10);
        int second = digit(cpf.substring(0, 9) + first, 11);
        return cpf.charAt(9) - '0' == first && cpf.charAt(10) - '0' == second;
    }

    private int digit(String numbers, int initialWeight) {
        int sum = 0;
        for (int i = 0; i < numbers.length(); i++) sum += (numbers.charAt(i) - '0') * (initialWeight - i);
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }
}
