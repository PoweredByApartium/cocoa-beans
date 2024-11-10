package net.apartium.cocoabeans.commands.lexer;

public class TestKeywordToken extends KeywordToken {
    /**
     * Create a new test keyword token
     * @param from starting index
     * @param to   ending index
     * @param keyword only keyword for test
     */
    protected TestKeywordToken(int from, int to, String keyword) {
        super(from, to, keyword);
    }

    @Override
    public String getKeyword() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null)
            return false;

        if (!(obj instanceof KeywordToken that))
            return false;

        return from == that.from && to == that.to && getKeyword().equals(that.getKeyword());
    }
}
