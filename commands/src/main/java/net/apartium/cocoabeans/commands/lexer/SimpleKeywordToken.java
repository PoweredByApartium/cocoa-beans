package net.apartium.cocoabeans.commands.lexer;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * A simple keyword token that represents a keyword
 */
@ApiStatus.AvailableSince("0.0.37")
public class SimpleKeywordToken extends KeywordToken {

    private final String keyword;

    /**
     * Create a new keyword token
     * @param from starting index
     * @param to ending index
     * @param text the entire text of command
     */
    public SimpleKeywordToken(int from, int to, String text) {
        super(from, to, text);

        this.keyword = text.substring(from, to);
    }


    /**
     * Get the keyword
     * @return the keyword
     */
    @Override
    public String getKeyword() {
        return keyword;
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
        if (o == null || getClass() != o.getClass()) {
            if (o instanceof KeywordToken keywordToken)
                return keywordToken.getKeyword().equals(keyword) && keywordToken.from() == from && keywordToken.to() == to;
        }
        SimpleKeywordToken that = (SimpleKeywordToken) o;
        return Objects.equals(from, that.from) && Objects.equals(to, that.to) && Objects.equals(text, that.text) && Objects.equals(keyword, that.keyword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, text, keyword);
    }
}
