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

    /**
     * @return Parser factory implementation associated with the annotated class
     */
    Class<? extends ParserFactory> value();

    /**
     * @return The scope of the parser to be created
     * @see Scope#VARIANT to apply to a specific sub command
     * @see Scope#CLASS to apply to the whole declaring class
     * @see Scope#ALL to apply to both cases mentioned above
     */
    Scope scope() default Scope.VARIANT;

}
