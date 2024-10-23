package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

/**
 * Quoted string parser
 * Parsers that let you press string that is more than one word and has a limit by using quotation marks
 * <br/><b>Examples</b>
 * <br/>
 * Input <code> /test hello </code>
 * Output <code> hello </code>
 * <br/>
 * Input <code> /test "hello world" </code>
 * Output <code> hello world </code>
 * @see LongStringParser
 */
@ApiStatus.AvailableSince("0.0.36")
public class QuotedStringParser extends LongStringParser {

    public static final String DEFAULT_KEYWORD = "quoted-string";

    /**
     * Creates quoted string parser
     * @param priority priority of the parser
     * @param keyword keyword that should be used
     */
    public QuotedStringParser(int priority, String keyword) {
        super(priority, keyword, false);
    }

    /**
     * Creates quoted string parser
     * @param priority priority of the parser
     */
    public QuotedStringParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }
}
