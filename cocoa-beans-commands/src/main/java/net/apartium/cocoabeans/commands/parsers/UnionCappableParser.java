package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.54")
public interface UnionCappableParser<T> {

    ArgumentParser<T> union(ArgumentParser<?>... parsers);

}
