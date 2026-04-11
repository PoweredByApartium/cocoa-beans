/*
 * Copyright (c) 2026, Lior Slakman (me@voigon.dev), ALL RIGHTS RESERVED
 * Do not use, copy, modify, and/or distribute this software without explicit permission from the
 * rights holder. Reselling this product is not allowed. Transfer of the source code to any person
 * or organization not explicitly approved by the rights holder via a license agreement is hereby forbidden.
 */

package net.apartium.cocoabeans.commands.spigot.parsers;

import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.MapBasedParser;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidParserResponse;
import net.apartium.cocoabeans.commands.spigot.parsers.exception.NoSuchWorldTypeResponse;
import org.bukkit.WorldType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * A parser for mapping world type names to {@link WorldType} objects.
 * This parser is based on a map where the keys are world type names,
 * and the values are corresponding {@link WorldType} instances.
 * It allows customization of keyword, priority, case sensitivity, and lax mode.
 */
@ApiStatus.AvailableSince("0.0.49")
public class WorldTypeParser extends MapBasedParser<WorldType> {

    /**
     * The default keyword used by this parser if none is specified.
     */
    public static final String DEFAULT_KEYWORD = "world-type";

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
        this(keyword, priority, false);
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
        return Map.of(
                "normal", WorldType.NORMAL,
                "flat", WorldType.FLAT,
                "large-biomes", WorldType.LARGE_BIOMES,
                "amplified", WorldType.AMPLIFIED
        );
    }

    @Override
    protected InvalidParserResponse createNoSuchElementResponse(CommandProcessingContext context, String message, String attempted) {
        return new NoSuchWorldTypeResponse(context, this, message, attempted);
    }
}
