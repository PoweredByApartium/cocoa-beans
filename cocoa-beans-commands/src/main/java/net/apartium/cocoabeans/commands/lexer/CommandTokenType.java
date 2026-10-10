package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

/**
 * CommandTokenType represents the type of command token
 */
@ApiStatus.AvailableSince("0.0.37")
public enum CommandTokenType {
    KEYWORD,
    ARGUMENT_PARSER,

    @ApiStatus.AvailableSince("0.0.54")
    UNION_ARGUMENT_PARSER,
}
