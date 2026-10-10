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

/* package-private */ final class CommandContextImpl implements CommandContext {
    private final Sender sender;
    private final @Nullable CommandInfo rawCommandInfo;
    private final @Nullable CommandOption option;
    private final @Nullable BadCommandResponse rawError;
    private final String[] args;
    private final String commandName;
    private final Map<Class<?>, List<Object>> parsedArgs;

    private NamedArgumentResult namedArgumentResult;

    CommandContextImpl(
            Sender sender,
            @Nullable CommandInfo rawCommandInfo,
            @Nullable CommandOption option,
            @Nullable BadCommandResponse rawError,
            String[] args,
            String commandName,
            Map<Class<?>, List<Object>> parsedArgs
    ) {
        this.sender = sender;
        this.rawCommandInfo = rawCommandInfo;
        this.option = option;
        this.rawError = rawError;
        this.args = args;
        this.commandName = commandName;
        this.parsedArgs = parsedArgs;
    }

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

    private NamedArgumentResult namedArgumentResult() {
        if (namedArgumentResult == null)
            namedArgumentResult = Optional.ofNullable(parsedArgs.get(NamedArgumentResult.class))
                    .map(List::getFirst)
                    .map(obj -> {
                        if (obj instanceof NamedArgumentResult result)
                            return result;
                        if (obj instanceof Optional<?> optional)
                            return optional.orElse(null);

                        return null;
                    })
                    .map(NamedArgumentResult.class::cast)
                    .orElseGet(() -> new NamedArgumentResult(Set.of(), Map.of()));

        return namedArgumentResult;
    }

    @Override
    public <T> OptionArgumentValue<T> option(OptionArgument<T> option) {
        return (OptionArgumentValue<T>) Optional.ofNullable(namedArgumentResult.options().get(option))
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
        return Set.copyOf(namedArgumentResult().options().values());
    }

    @Override
    public Set<FlagArgument> flags() {
        return namedArgumentResult().flags();
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

    @Override
    public Sender sender() {
        return sender;
    }

    public @Nullable CommandInfo rawCommandInfo() {
        return rawCommandInfo;
    }

    public @Nullable CommandOption option() {
        return option;
    }

    public @Nullable BadCommandResponse rawError() {
        return rawError;
    }

    @Override
    public String commandName() {
        return commandName;
    }

    public Map<Class<?>, List<Object>> parsedArgs() {
        return parsedArgs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CommandContextImpl) obj;
        return Objects.equals(this.sender, that.sender) &&
                Objects.equals(this.rawCommandInfo, that.rawCommandInfo) &&
                Objects.equals(this.option, that.option) &&
                Objects.equals(this.rawError, that.rawError) &&
                Arrays.equals(this.args, that.args) &&
                Objects.equals(this.commandName, that.commandName) &&
                Objects.equals(this.parsedArgs, that.parsedArgs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, rawCommandInfo, option, rawError, Arrays.hashCode(args), commandName, parsedArgs);
    }

}