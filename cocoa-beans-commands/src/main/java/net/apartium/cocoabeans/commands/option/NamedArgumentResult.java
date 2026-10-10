package net.apartium.cocoabeans.commands.option;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

@ApiStatus.AvailableSince("0.0.54")
public record NamedArgumentResult(Set<FlagArgument> flags, Map<OptionArgument<?>, OptionArgumentValue<?>> options) {

    public NamedArgumentResult(Set<FlagArgument> flags, Map<OptionArgument<?>, OptionArgumentValue<?>> options) {
        this.flags = Set.copyOf(flags);
        this.options = Collections.unmodifiableMap(new IdentityHashMap<>(options));
    }

}
