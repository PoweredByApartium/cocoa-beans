package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandNode;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("0.0.30")
public interface ParserFactory {

    @Nullable
    ArgumentParser<?> getArgumentParser(CommandNode commandNode, Object annotation, Object obj);

}
