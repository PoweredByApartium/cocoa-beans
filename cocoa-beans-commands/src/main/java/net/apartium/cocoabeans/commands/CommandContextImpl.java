/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.option.FlagArgument;
import net.apartium.cocoabeans.commands.option.NamedArgumentResult;
import net.apartium.cocoabeans.commands.option.OptionArgument;
import net.apartium.cocoabeans.commands.option.OptionArgumentValue;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/* package-private */ record CommandContextImpl(
        Sender sender,
        @Nullable CommandInfo rawCommandInfo,
        @Nullable CommandOption option,
        @Nullable BadCommandResponse rawError,
        String[] args,
        String commandName,
        Map<Class<?>, List<Object>> parsedArgs
) implements CommandContext {

    @Override
    public Optional<CommandInfo> commandInfo() {
        return Optional.ofNullable(rawCommandInfo);
    }

    @Override
    public Optional<BadCommandResponse> error() {
        return Optional.ofNullable(rawError);
    }

    @Override
    public String[] args() {
        String[] argsTmp = new String[args.length];
        System.arraycopy(args, 0, argsTmp, 0, args.length);
        return argsTmp;
    }

    @Override
    public <T> OptionArgumentValue<T> option(OptionArgument<T> option) {
        return (OptionArgumentValue<T>) Optional.ofNullable(parsedArgs.get(NamedArgumentResult.class))
                .map(List::getFirst)
                .map(NamedArgumentResult.class::cast)
                .map(n -> n.options().get(option))
                .orElseGet(() -> new OptionArgumentValue<>(
                        null,
                        null,
                        null
                ));
    }

    @Override
    public boolean flag(FlagArgument flag) {
        return flags().contains(flag);
    }

    @Override
    public Set<OptionArgumentValue<?>> options() {
        return Optional.ofNullable(parsedArgs.get(NamedArgumentResult.class))
                .map(List::getFirst)
                .map(NamedArgumentResult.class::cast)
                .map(NamedArgumentResult::options)
                .map(Map::values)
                .map(Set::copyOf)
                .orElseGet(Set::of);
    }

    @Override
    public Set<FlagArgument> flags() {
        return Optional.ofNullable(parsedArgs.get(NamedArgumentResult.class))
                .map(List::getFirst)
                .map(NamedArgumentResult.class::cast)
                .map(NamedArgumentResult::flags)
                .orElseGet(Set::of);
    }

    @Override
    public @NonNull String toString() {
        return "CommandContext{" +
                "sender=" + sender +
                ", commandInfo=" + rawCommandInfo +
                ", option=" + option +
                ", error=" + rawError +
                ", args=" + Arrays.toString(args) +
                ", commandName='" + commandName + '\'' +
                ", parsedArgs=" + parsedArgs +
                '}';
    }
}