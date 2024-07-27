package net.apartium.cocoabeans.commands.parsers;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ParserVariants.class)
public @interface ParserVariant {

    String value();
    int priority() default 0;

}
