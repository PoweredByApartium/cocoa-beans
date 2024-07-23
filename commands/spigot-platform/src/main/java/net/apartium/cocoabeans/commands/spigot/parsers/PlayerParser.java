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
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerParser extends ArgumentParser<Player> {

    public PlayerParser(int priority) {
        super("player", Player.class, priority);
    }

    @Override
    public Optional<ParseResult<Player>> parse(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        Player player = Bukkit.getPlayerExact(args.get(startIndex));
        if (player == null)
            return Optional.empty();

        if (processingContext.sender() instanceof Player sender && !sender.canSee(player))
            return Optional.empty();

        if (processingContext.sender().getSender() != null && processingContext.sender().getSender() instanceof Player sender && !sender.canSee(player))
            return Optional.empty();


            return Optional.of(new ParseResult<>(
                player,
                startIndex + 1
        ));
    }

    @Override
    public OptionalInt tryParse(CommandProcessingContext processingContext) {
        return parse(processingContext).map(ParseResult::newIndex).map(OptionalInt::of).orElse(OptionalInt.empty());
    }

    @Override
    public Optional<TabCompletionResult> tabCompletion(CommandProcessingContext processingContext) {
        List<String> args = processingContext.args();
        int startIndex = processingContext.index();

        Stream<? extends Player> stream = Bukkit.getOnlinePlayers().stream();

        if (processingContext.sender() instanceof Player sender)
            stream = stream.filter(sender::canSee);

        return Optional.of(new TabCompletionResult(stream
                .map(Player::getName)
                .filter(s -> s.toLowerCase().startsWith(args.get(startIndex).toLowerCase()))
                .collect(Collectors.toSet()),
                startIndex + 1
        ));
    }
}
