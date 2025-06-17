package net.apartium.cocoabeans.commands.virtual;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @see VirtualMetadata
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@ApiStatus.Experimental
public @interface VirtualMetadataCollection {

    /**
     * @see VirtualMetadata
     */
    VirtualMetadata[] value();

}
