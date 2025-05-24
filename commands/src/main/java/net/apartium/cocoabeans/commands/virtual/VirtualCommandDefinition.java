package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * A DTO for commands, a serializable form of commands used to send command definition between applications
 * @see VirtualMetadata
 * @see VirtualCommandFactory
 */
@ApiStatus.AvailableSince("0.0.39")
public record VirtualCommandDefinition(
        String name,
        Set<String> aliases,
        CommandInfo info,
        Set<VirtualCommandVariant> variants,
        Map<String, Object> metadata
) implements GenericNode {

    /**
     * Create a new virtual command definition instance
     * @param name name
     * @param aliases aliases
     * @param info info
     * @param variants variants
     * @param metadata metadata
     */
    public VirtualCommandDefinition(String name, Set<String> aliases, CommandInfo info, Set<VirtualCommandVariant> variants, Map<String, Object> metadata) {
        this.name = name;
        this.aliases = Set.copyOf(aliases);
        this.info = info;
        this.variants = Set.copyOf(variants);
        this.metadata = Map.copyOf(metadata);
    }
}
