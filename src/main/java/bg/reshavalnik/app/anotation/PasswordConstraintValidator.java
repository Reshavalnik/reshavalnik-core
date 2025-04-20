package bg.reshavalnik.app.anotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import org.passay.*;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {}

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator passwordValidator =
                new PasswordValidator(
                        Arrays.asList(
                                new LengthRule(6, 30),
                                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                                new CharacterRule(EnglishCharacterData.Digit, 1),
                                new CharacterRule(EnglishCharacterData.Special, 1),
                                new WhitespaceRule(),
                                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                                new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)));

        RuleResult ruleResult = passwordValidator.validate(new PasswordData(password));

        if (ruleResult.isValid()) {
            return true;
        }

        List<String> messages = passwordValidator.getMessages(ruleResult);
        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
