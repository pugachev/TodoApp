package jp.co.security.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Size;

@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Size(min = 1, max = 30) // 最小・最大7桁の@Sizeを指定
public @interface Content {
    String message() default "{content.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target(java.lang.annotation.ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public static @interface List {
    	Content[] value();
    }
}
