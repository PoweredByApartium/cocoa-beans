package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Parser variant is a variant of compound parser
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ParserVariants.class)
@ApiStatus.AvailableSince("0.0.37")
public @interface ParserVariant {

    String value();
    int priority() default 0;

}
