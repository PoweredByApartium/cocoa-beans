package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotations should be annotated with this annotation to specify their underlying factory impl
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.AvailableSince("0.0.30")
public @interface CommandParserFactory {

    Class<? extends ParserFactory> value();
    Scope scope() default Scope.VARIANT;

}
