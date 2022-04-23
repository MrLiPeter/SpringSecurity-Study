package uaa.validation;

import uaa.annotation.ValidPasswordMatch;
import uaa.domain.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class PasswordMatch implements ConstraintValidator<ValidPasswordMatch,UserDto> {

    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        return userDto.getPassword().equals(userDto.getMatchPassword());
    }

}
