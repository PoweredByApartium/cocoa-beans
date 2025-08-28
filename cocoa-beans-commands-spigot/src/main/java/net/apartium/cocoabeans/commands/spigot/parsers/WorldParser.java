package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * A parser for mapping world names to {@link World} objects.
 * This parser is based on a map where the keys are world names,
 * and the values are corresponding {@link World} instances.
 * It allows customization of keyword, priority, case sensitivity, and lax mode.
 */
@ApiStatus.AvailableSince("0.0.38")
public class WorldParser extends MapBasedParser<World> {

    /**
     * The default keyword used by this parser if none is specified.
     */
    public static final String DEFAULT_KEYWORD = "world";

    /**
     * Constructs a new {@code WorldParser} with the specified parameters.
     *
     * @param keyword the keyword associated with this parser.
     * @param priority the priority of the parser, used for ordering.
     * @param ignoreCase whether the parser should ignore case when matching keys.
     * @param lax whether the parser should operate in lax mode,
     * allowing more flexible parsing behavior.
     */
    public WorldParser(String keyword, int priority, boolean ignoreCase, boolean lax) {
        super(keyword, World.class, priority, ignoreCase, lax);
    }

    /**
     * Constructs a new {@code WorldParser} with the specified parameters.
     * Assumes lax mode is disabled.
     *
     * @param keyword the keyword associated with this parser.
     * @param priority the priority of the parser, used for ordering.
     * @param ignoreCase whether the parser should ignore case when matching keys.
     */
    public WorldParser(String keyword, int priority, boolean ignoreCase) {
        this(keyword, priority, ignoreCase, false);
    }

    /**
     * Constructs a new {@code WorldParser} with the specified keyword and priority.
     * Assumes case sensitivity and lax mode are disabled.
     *
     * @param keyword the keyword associated with this parser.
     * @param priority the priority of the parser, used for ordering.
     */
    public WorldParser(String keyword, int priority) {
        this(keyword, priority, false);
    }

    /**
     * Constructs a new {@code WorldParser} with the default keyword and the specified priority.
     * Assumes case sensitivity and lax mode are disabled.
     *
     * @param priority the priority of the parser, used for ordering.
     */
    public WorldParser(int priority) {
        this(DEFAULT_KEYWORD, priority);
    }

    /**
     * Provides a map of world names to their corresponding {@link World} instances.
     *
     * @return a {@link Map} where the keys are the names of all loaded worlds,
     * and the values are the corresponding {@link World} instances.
     */
    @Override
    public Map<String, World> getMap() {
        return Bukkit.getWorlds()
                .stream()
                .collect(Collectors.toMap((w) -> w.getName(), world -> world));
    }

}
