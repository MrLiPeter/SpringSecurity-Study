package uaa.annotation;

import uaa.validation.EmailValidator;
import uaa.validation.PasswordMatch;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.FIELD,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatch.class)
@Documented
public @interface ValidPasswordMatch {
    String message() default "Password not Match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
