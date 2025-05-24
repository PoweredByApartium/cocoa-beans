package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.CommandInfo;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * Represents a variant in a virtual command
 * @param info command info
 * @param variant variant matcher
 * @param ignoreCase ignore case for matcher
 * @param metadata variant metadata
 * @see VirtualCommandDefinition
 */
@ApiStatus.AvailableSince("0.0.39")
public record VirtualCommandVariant(
        CommandInfo info,
        String variant,
        boolean ignoreCase,
        Map<String, Object> metadata
) {

    /**
     * Creates a variant in a virtual command
     * @param info command info
     * @param variant variant matcher
     * @param ignoreCase ignore case for matcher
     * @param metadata variant metadata
     */
    public VirtualCommandVariant(CommandInfo info, String variant, boolean ignoreCase, Map<String, Object> metadata) {
        this.info = info;
        this.variant = variant;
        this.ignoreCase = ignoreCase;
        this.metadata = Map.copyOf(metadata);
    }

}
