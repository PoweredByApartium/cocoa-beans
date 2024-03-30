package net.apartium.cocoabeans.commands.parsers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SourceParser {

    String keyword();
    int priority() default 0;
    Class<?> clazz();

    // -1 is infinite
    // 0 is disabled keeping
    long resultMaxAgeInMills() default 0;

}
