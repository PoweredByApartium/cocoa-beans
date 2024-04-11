package net.apartium.cocoabeans.commands.parsers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a source parser for a specific method that give that class access to it
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SourceParser {

    /**
     * The keyword that triggers the parser
     */
    String keyword();

    /**
     * The priority
     */
    int priority() default 0;

    /**
     * The return type of the parser
     */
    Class<?> clazz();

    /**
     * The max age of the result in milliseconds
     * When the result is older it will be discarded next time someone use the parser
     * When set to -1 it will never be discarded
     * When set to 0 it will always be discarded
     */
    long resultMaxAgeInMills() default 0;

}
