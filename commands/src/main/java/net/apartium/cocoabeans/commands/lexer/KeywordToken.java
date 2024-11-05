package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.37")
public class KeywordToken extends CommandToken {

    protected KeywordToken(int from, int to, String text) {
        super(from, to, text);
    }


    public String getKeyword() {
        return text.substring(from, to);
    }

    @Override
    public CommandTokenType getType() {
        return CommandTokenType.KEYWORD;
    }

}
