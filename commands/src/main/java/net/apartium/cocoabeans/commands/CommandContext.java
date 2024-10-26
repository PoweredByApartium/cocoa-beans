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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Represents the context of a command after it has been processed
 * @param sender The sender of the command
 * @param option The command option that was evaluated
 * @param args commands raw args
 * @param commandName command name
 * @param parsedArgs parsed args
 */
// TODO change to interface
public record CommandContext(Sender sender,
                             CommandInfo commandInfo,
                             @Nullable CommandOption option,
                             @Nullable BadCommandResponse error,
                             String[] args,
                             String commandName,
                             Map<Class<?>, List<Object>> parsedArgs
) {

    boolean hasError() {
        return error != null;
    }

    @ApiStatus.AvailableSince("0.0.36")
    ArgumentContext toArgumentContext() {
        return new ArgumentContext(
                commandName,
                args,
                sender,
                parsedArgs
        );
    }

    @Override
    public String toString() {

        Map<Class<?>, List<Object>> classListMap = new HashMap<>(parsedArgs);
        classListMap.remove(CommandContext.class);

        return "CommandContext{" +
                "sender=" + sender +
                ", commandInfo=" + commandInfo +
                ", option=" + option +
                ", error=" + error +
                ", args=" + Arrays.toString(args) +
                ", commandName='" + commandName + '\'' +
                ", parsedArgs=" + classListMap +
                '}';
    }
}
