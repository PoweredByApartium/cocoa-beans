/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.commands.spigot;

import net.apartium.cocoabeans.commands.ArgumentIndex;
import net.apartium.cocoabeans.commands.MapConverter;
import net.apartium.cocoabeans.commands.SimpleArgumentMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SpigotArgumentMapper extends SimpleArgumentMapper {

    public SpigotArgumentMapper() {
        super();
    }

    public SpigotArgumentMapper(List<MapConverter<?>> converters) {
        super(converters);
    }

    @Override
    protected ArgumentIndex<?> resolveBuiltInArgumentIndex(Class<?> type, Map<Class<?>, Integer> counterMap, Map<Class<?>, List<ArgumentIndex<?>>> mapOfArguments, int index) {
        ArgumentIndex<?> argumentIndex = super.resolveBuiltInArgumentIndex(type, counterMap, mapOfArguments, index);
        if (argumentIndex != null)
            return argumentIndex;

        for (MapConverter<?> converter : converters) {
            if (!converter.targetType().isAssignableFrom(type))
                continue;

            if (converter.isSourceTypeSupported(CommandSender.class))
                return map(converter, getSpigotSenderIndex(CommandSender.class, counterMap, mapOfArguments, index));
        }

        if (CommandSender.class.isAssignableFrom(type))
            return getSpigotSenderIndex(type, counterMap, mapOfArguments, index);

        return null;
    }

    protected ArgumentIndex<?> getSpigotSenderIndex(Class<?> type, Map<Class<?>, Integer> counterMap, Map<Class<?>, List<ArgumentIndex<?>>> mapOfArguments, int index) {
        if (type == CommandSender.class && index == 0)
            return context -> ((CommandSender) context.sender().getSender());

        boolean hasSender = counterMap.getOrDefault(CommandSender.class, 0) != 0;

        if (type == Player.class && !hasSender) {
            counterMap.put(CommandSender.class, 1);
            counterMap.put(Player.class, -1);
            return context -> {
                if (!(context.sender().getSender() instanceof Player player))
                    throw new IllegalArgumentException("Sender is not a player");

                return player;
            };
        }

        return mapOfArguments.get(type).get(index);
    }

}
