package bg.reshavalnik.app.anotataion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import bg.reshavalnik.app.anotation.PasswordConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PasswordConstraintValidatorTest {

    @InjectMocks private PasswordConstraintValidator validator;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConstraintValidatorContext context;

    @Mock private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doReturn(builder).when(context).buildConstraintViolationWithTemplate(anyString());
        doReturn(context).when(builder).addConstraintViolation();
    }

    @Test
    void nullPassword_ShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> validator.isValid(null, context));
    }

    @Test
    void validPassword_ShouldReturnTrue_AndNeverBuildViolation() {
        boolean valid = validator.isValid("Abc!23", context);
        assertTrue(valid);
        verify(context, never()).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void tooShortPassword_ShouldReturnFalse_AndBuildViolation() {
        boolean valid = validator.isValid("A1!a", context);
        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate(anyString());
        verify(builder).addConstraintViolation();
    }

    @Test
    void missingUppercase_ShouldReturnFalse_AndBuildViolation() {
        boolean valid = validator.isValid("abcd123!", context);
        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void missingDigit_ShouldReturnFalse_AndBuildViolation() {
        boolean valid = validator.isValid("AbcdEf!@", context);
        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void missingSpecialChar_ShouldReturnFalse_AndBuildViolation() {
        boolean valid = validator.isValid("Abcd1234", context);
        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void whitespace_ShouldReturnFalse_AndBuildViolation() {
        boolean valid = validator.isValid("Abcd 123!", context);
        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }

    @Test
    void illegalSequence_ShouldReturnFalse_AndBuildViolation() {
        boolean valid = validator.isValid("Abcde1!", context);
        assertFalse(valid);
        verify(context).buildConstraintViolationWithTemplate(anyString());
    }
}
