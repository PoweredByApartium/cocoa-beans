package net.apartium.cocoabeans.commands.parsers;

import org.jetbrains.annotations.ApiStatus;

/**
 * Parser that let you press string that is more than one word and has a limit by using quotation marks also has support for new lines
 * <br/><b>Example</b><br/>
 * Input <code>/test Hello\nWorld </code>
 * <br/>Output
 * <code>
 *     Hello
 *     <br/>
 *     World
 * </code>
 * @see LongStringParser
 */
@ApiStatus.AvailableSince("0.0.36")
public class ParagraphParser extends LongStringParser {

    public static final String DEFAULT_KEYWORD = "paragraph";

    /**
     * Creates paragraph parser
     * @param priority priority of the parser
     * @param keyword keyword that should be used
     */
    public ParagraphParser(int priority, String keyword) {
        super(priority, keyword, true);
    }

    /**
     * Creates paragraph parser
     * @param priority priority of the parser
     */
    public ParagraphParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

}
