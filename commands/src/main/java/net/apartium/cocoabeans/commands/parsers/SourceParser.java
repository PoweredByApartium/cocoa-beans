package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a source parser for a specific method that give that class access to it
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@CommandParserFactory(value = SourceParserFactory.class, global = true)
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

    /**
     * Whether this variant should ignore case for its label
     */
    @ApiStatus.AvailableSince("0.0.30")
    boolean ignoreCase() default false;

    /**
     * Whether this variant should lazy map for its label
     */
    @ApiStatus.AvailableSince("0.0.30")
    boolean lax() default false;

}
