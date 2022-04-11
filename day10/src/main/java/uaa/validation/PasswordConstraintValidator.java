package uaa.validation;

import lombok.val;
import org.passay.*;
import uaa.annotation.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;


public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword,String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        val validator =  new PasswordValidator(Arrays.asList(new LengthRule(8,30),
                new CharacterRule(EnglishCharacterData.UpperCase,1),//要有最少1个大写字母
                new CharacterRule(EnglishCharacterData.Special,1),//要有特殊字符
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical,5,false),//不能有5个连续字符abcde
                new IllegalSequenceRule(EnglishSequenceData.Numerical,5,false),//不能有5个连续的数字
                new IllegalSequenceRule(EnglishSequenceData.USQwerty,5,false),//不能有键盘上连续5个字符qwert
                new WhitespaceRule()//要带有特殊字符
        ));
        val ruleResult = validator.validate(new PasswordData(password));
        return ruleResult.isValid();
    }
}
