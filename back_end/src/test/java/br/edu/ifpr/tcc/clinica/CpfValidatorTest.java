package br.edu.ifpr.tcc.clinica;

import br.edu.ifpr.tcc.clinica.validation.CpfValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CpfValidatorTest {
    private final CpfValidator validator = new CpfValidator();
    @Test void acceptsValidCpfWithOrWithoutFormatting() {
        assertTrue(validator.isValid("52998224725", null));
        assertTrue(validator.isValid("529.982.247-25", null));
    }
    @Test void rejectsInvalidAndRepeatedCpf() {
        assertFalse(validator.isValid("12345678901", null));
        assertFalse(validator.isValid("11111111111", null));
        assertFalse(validator.isValid("12.34", null));
    }
}
