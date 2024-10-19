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

import net.apartium.cocoabeans.StringHelpers;
import net.apartium.cocoabeans.commands.CommandProcessingContext;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.utils.OptionalFloat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

public class LocationParser extends ArgumentParser<Location> {

    public static final String DEFAULT_KEYWORD = "location";

    /**
     * Constructs a new instance of LocationParser
     * @param priority parser priority of which should be higher than others or lower
     * @param keyword parser keyword
     */
    @ApiStatus.AvailableSince("0.0.36")
    public LocationParser(int priority, String keyword) {
        super(keyword, Location.class, priority);
    }

    /**
     * Constructs a new instance of LocationParser
     * @param priority parser priority of which should be higher than others or lower
     */
    public LocationParser(int priority) {
        this(priority, DEFAULT_KEYWORD);
    }

    @Override
    public Optional<ParseResult<Location>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args(); // pre 20 20 20 20
        int index = processingContext.index(); // @SubCommand("pre <location>) 1
        int leftArgs = args.size() - index;
        if (leftArgs < 3)
            return Optional.empty();

        World world = getSenderWorld(processingContext).orElse(null);
        OptionalDouble optionalX = StringHelpers.parseDouble(args.get(index));
        if (optionalX.isEmpty()) {

            // if first arg is not numeric, x y z fields shift one argument, so the minimum number of args is 4 and not 3
            if (leftArgs < 4)
                return Optional.empty();

            world = Bukkit.getWorld(args.get(index++));
            optionalX = StringHelpers.parseDouble(args.get(index));
        }

        index++;
        OptionalDouble optionalY = StringHelpers.parseDouble(args.get(index++));
        OptionalDouble optionalZ = StringHelpers.parseDouble(args.get(index++));

        if (world == null || optionalX.isEmpty() || optionalY.isEmpty() || optionalZ.isEmpty())
            return Optional.empty();

        Location location = new Location(world, optionalX.getAsDouble(), optionalY.getAsDouble(), optionalZ.getAsDouble());
        if ((args.size() - index) < 2)
            return Optional.of(new ParseResult<>(location, index));

        OptionalFloat optionalYaw = StringHelpers.parseFloat(args.get(index++));
        OptionalFloat optionalPitch = StringHelpers.parseFloat(args.get(index));

        if (optionalYaw.isEmpty() || optionalPitch.isEmpty())
            return Optional.of(new ParseResult<>(location, index - 1));

        location.setYaw(optionalYaw.getAsFloat());
        location.setPitch(optionalPitch.getAsFloat());
        
        return Optional.of(new ParseResult<>(location, index + 1));
    }

    private Optional<World> getSenderWorld(CommandProcessingContext processingContext) {
        if (processingContext.sender().getSender() instanceof BlockCommandSender console)
            return Optional.of(console.getBlock().getWorld());
        else if (processingContext.sender().getSender() instanceof Entity entity)
            return Optional.of(entity.getWorld());
        else
            return Optional.empty();
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.size() - index < 4)
            return OptionalInt.empty();

        if (StringHelpers.parseDouble(args.get(index + 1)).isEmpty() ||
                StringHelpers.parseDouble(args.get(index + 2)).isEmpty() ||
                StringHelpers.parseDouble(args.get(index + 3)).isEmpty()
        ) return OptionalInt.empty();

        if (Bukkit.getWorld(args.get(index)) == null)
            return OptionalInt.empty();

        return OptionalInt.of(index + 4);
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int index = processingContext.index();

        if (args.isEmpty())
            return Optional.of(new TabCompletionResult(
                    Bukkit.getWorlds().stream()
                            .map(World::getName)
                            .collect(Collectors.toSet()),
                    index + 1
            ));

        if (args.size() - index == 0)
            return Optional.of(new TabCompletionResult(
                    Bukkit.getWorlds().stream()
                            .map(World::getName)
                            .filter(worldName -> args.get(index).startsWith(worldName))
                            .collect(Collectors.toSet()),
                    index + 1
            ));

        if (args.size() - index == 1) {
            if (Bukkit.getWorld(args.get(index)) == null)
                return Optional.empty();
        }

        if (args.size() - index == 2)
            return tabDouble(args.get(index + 1), index + 2);

        if (args.size() - index == 3) {
            if (StringHelpers.parseDouble(args.get(index + 1)).isEmpty())
                return Optional.empty();

            return tabDouble(args.get(index + 2), index + 3);
        }

        if (args.size() - index == 4) {
            if (StringHelpers.parseDouble(args.get(index + 1)).isEmpty())
                return Optional.empty();

            if (StringHelpers.parseDouble(args.get(index + 2)).isEmpty())
                return Optional.empty();

            return tabDouble(args.get(index + 3), index + 4);
        }

        return Optional.empty();
    }

    private Optional<TabCompletionResult> tabDouble(String s, int resultIndex) {
        if (s.isEmpty())
            return Optional.of(new TabCompletionResult(
                    Set.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-", "."),
                    resultIndex
            ));

        boolean onlyZero = true;
        boolean hasDot = false;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '-' || s.charAt(i) == '+') {
                if (i != 0)
                    return Optional.empty();
                continue;
            }

            if (s.charAt(i) == '.') {
                if (hasDot)
                    return Optional.empty();
                hasDot = true;
                onlyZero = false;
                continue;
            }

            if (s.charAt(i) == '0') {
                onlyZero = false;
                continue;
            }

            if (s.charAt(i) >= '0' && s.charAt('9') <= 9)
                continue;

            return Optional.empty();
        }

        Set<String> result = new HashSet<>();

        for (int i = onlyZero ? 1 : 0; i < 10; i++) {
            result.add(s + i);
        }

        if (!hasDot)
            result.add(s + ".");

        return Optional.of(new TabCompletionResult(
                result,
                resultIndex
        ));
    }
}
