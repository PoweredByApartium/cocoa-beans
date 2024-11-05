package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@ApiStatus.AvailableSince("0.0.37")
public class KeywordToken extends CommandToken {

    private final String keyword;

    protected KeywordToken(int from, int to, String text) {
        super(from, to, text);

        this.keyword = text.substring(from, to);
    }


    public String getKeyword() {
        return keyword;
    }

    @Override
    public CommandTokenType getType() {
        return CommandTokenType.KEYWORD;
    }


    @Override
    public String toString() {
        return "KeywordToken{" +
                "keyword='" + keyword + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", text='" + text + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeywordToken that = (KeywordToken) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(text, that.text) && Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, keyword);
    }
}
