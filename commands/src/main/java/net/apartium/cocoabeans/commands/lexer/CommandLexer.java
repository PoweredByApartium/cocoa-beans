package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.AvailableSince("0.0.37")
public interface CommandLexer {

    List<CommandToken> tokenization(String command);

}
