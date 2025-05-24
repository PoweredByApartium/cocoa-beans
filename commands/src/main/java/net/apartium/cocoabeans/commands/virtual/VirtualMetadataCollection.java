package net.apartium.cocoabeans.commands.virtual;

import java.lang.annotation.*;

/**
 * @see VirtualMetadata
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface VirtualMetadataCollection {

    /**
     * @see VirtualMetadata
     */
    VirtualMetadata[] value();

}
