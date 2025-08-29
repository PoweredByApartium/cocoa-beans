package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

/**
 * Represents the scope of a parser factory
 * @see ParserFactory
 */
@ApiStatus.AvailableSince("0.0.30")
public enum Scope {

    /**
     * Scoped to a specific sub command
     */
    VARIANT,

    /**
     * Scoped to an entire command class
     */
    CLASS,

    /**
     * Scoped to either scopes
     * @see Scope#VARIANT
     * @see Scope#CLASS
     */
    ALL;

    /**
     * Whether this scope applies to a class
     * @return true if applies to a class, else false
     * @see Scope#CLASS
     * @see Scope#ALL
     */
    public boolean isClass() {
        return this == CLASS || this == ALL;
    }

    /**
     * Whether this scope applies to a variant
     * @return true if applies to a variant, else false
     * @see Scope#VARIANT
     * @see Scope#ALL
     */
    public boolean isVariant() {
        return this == VARIANT || this == ALL;
    }

}
