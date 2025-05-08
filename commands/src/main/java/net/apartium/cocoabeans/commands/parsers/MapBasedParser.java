package net.apartium.cocoabeans.commands.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.exception.AmbiguousMappedKeyResponse;
import net.apartium.cocoabeans.commands.parsers.exception.NoSuchElementInMapResponse;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Map-based parser that map of keyword to value
 * When Map entry not found report {@link NoSuchElementInMapResponse}
 * Unlike {@link ContextualMapBasedParser} MapBasedParser doesn't care for context
 * @see SourceParser
 * @see ContextualMapBasedParser
 * @param <T> result type
 */
public abstract class MapBasedParser<T> extends ContextualMapBasedParser<T> {

    /**
     * Constructor
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     */
    public MapBasedParser(String keyword, Class<T> clazz, int priority) {
        this(keyword, clazz, priority, false);
    }

    /**
     * Constructor with ignoreCase
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     * @param ignoreCase whether is it case-sensitive or not
     */
    @ApiStatus.AvailableSince("0.0.30")
    public MapBasedParser(String keyword, Class<T> clazz, int priority, boolean ignoreCase) {
        this(keyword, clazz, priority, ignoreCase, false);
    }

    /**
     * Constructor with ignoreCase and lax
     * @param keyword keyword
     * @param clazz result class
     * @param priority priority
     * @param ignoreCase whether is it case-sensitive or not
     * @param lax lazy mapping
     */
    @ApiStatus.AvailableSince("0.0.30")
    public MapBasedParser(String keyword, Class<T> clazz, int priority, boolean ignoreCase, boolean lax) {
        super(keyword, clazz, priority, ignoreCase, lax);
    }

    /**
     * If you want to ignore case to be applied, then remember to lowercase the keyword
     * @return map of keyword to value
     */
    public abstract Map<String, T> getMap();

    @Override
    public Map<String, T> getMap(CommandProcessingContext context) {
        return getMap();
    }
}
