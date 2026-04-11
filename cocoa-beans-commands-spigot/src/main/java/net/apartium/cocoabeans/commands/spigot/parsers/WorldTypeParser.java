/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidParserResponse;
import net.apartium.cocoabeans.commands.spigot.parsers.exception.NoSuchWorldTypeResponse;
import org.bukkit.WorldType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A parser for mapping world type names to {@link WorldType} objects.
 * This parser is based on a map where the keys are world type names,
 * and the values are corresponding {@link WorldType} instances.
 * It allows customization of keyword, priority, case sensitivity, and lax mode.
 */
@ApiStatus.AvailableSince("0.0.49")
public class WorldTypeParser extends MapBasedParser<WorldType> {

    private static final Map<String, WorldType> WORLD_TYPES = Arrays.stream(WorldType.values())
            .flatMap(worldType -> Stream.of(
                    Map.entry(worldType.getName().toLowerCase(), worldType),
                    Map.entry(worldType.name().toLowerCase(), worldType)
            ))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (worldType1, worldType2) -> worldType1));

    /**
     * The default keyword used by this parser if none is specified.
     */
    private static final String DEFAULT_KEYWORD = "world-type";

    /**
     * Constructs a new {@code WorldTypeParser} with the specified parameters.
     *
     * @param keyword the keyword associated with this parser.
     * @param priority the priority of the parser, used for ordering.
     * @param ignoreCase whether the parser should ignore case when matching keys.
     * @param lax whether the parser should operate in lax mode,
     * allowing more flexible parsing behavior.
     */
    public WorldTypeParser(String keyword, int priority, boolean ignoreCase, boolean lax) {
        super(keyword, WorldType.class, priority, ignoreCase, lax);
    }

    /**
     * Constructs a new {@code WorldTypeParser} with the specified parameters.
     * Assumes lax mode is disabled.
     *
     * @param keyword the keyword associated with this parser.
     * @param priority the priority of the parser, used for ordering.
     * @param ignoreCase whether the parser should ignore case when matching keys.
     */
    public WorldTypeParser(String keyword, int priority, boolean ignoreCase) {
        this(keyword, priority, ignoreCase, false);
    }

    /**
     * Constructs a new {@code WorldTypeParser} with the specified keyword and priority.
     * Assumes case sensitivity and lax mode are disabled.
     *
     * @param keyword the keyword associated with this parser.
     * @param priority the priority of the parser, used for ordering.
     */
    public WorldTypeParser(String keyword, int priority) {
        this(keyword, priority, true);
    }

    /**
     * Constructs a new {@code WorldTypeParser} with the default keyword and the specified priority.
     * Assumes case sensitivity and lax mode are disabled.
     *
     * @param priority the priority of the parser, used for ordering.
     */
    public WorldTypeParser(int priority) {
        this(DEFAULT_KEYWORD, priority);
    }

    /**
     * Provides a map of world type names to their corresponding {@link WorldType} instances.
     *
     * @return a {@link Map} where the keys are the names of all world types,
     * and the values are the corresponding {@link WorldType} instances.
     */
    @Override
    public Map<String, WorldType> getMap() {
        return WORLD_TYPES;
    }

    @Override
    protected InvalidParserResponse createNoSuchElementResponse(CommandProcessingContext context, String message, String attempted) {
        return new NoSuchWorldTypeResponse(context, this, message, attempted);
    }
}
