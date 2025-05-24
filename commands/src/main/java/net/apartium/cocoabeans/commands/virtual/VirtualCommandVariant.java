package net.apartium.cocoabeans.commands.virtual;

import net.apartium.cocoabeans.commands.CommandInfo;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;


@ApiStatus.AvailableSince("0.0.39")
public record VirtualCommandVariant(
        CommandInfo info,
        String variant,
        boolean ignoreCase,
        Map<String, Object> metadata
) {

    public VirtualCommandVariant(CommandInfo info, String variant, boolean ignoreCase, Map<String, Object> metadata) {
        this.info = info;
        this.variant = variant;
        this.ignoreCase = ignoreCase;
        this.metadata = Map.copyOf(metadata);
    }

}
