package net.apartium.cocoabeans.commands.virtual;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.*;

/**
 * Specifies metadata for a command or command variant. Used for the virtual command system
 * @see VirtualCommandDefinition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(VirtualMetadataCollection.class)
@ApiStatus.AvailableSince("0.0.39")
public @interface VirtualMetadata {

    /**
     * @return Metadata key
     */
    String key();

    /**
     * @return Metadata value
     */
    String value();

}
