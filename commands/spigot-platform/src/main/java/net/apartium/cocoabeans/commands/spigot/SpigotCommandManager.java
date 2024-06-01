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

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.exception.CommandException;
import net.apartium.cocoabeans.commands.exception.ExceptionArgumentMapper;
import net.apartium.cocoabeans.commands.exception.UnknownCommandException;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.spigot.exception.SpigotExceptionArgumentMapper;
import net.apartium.cocoabeans.commands.spigot.parsers.MaterialParser;
import net.apartium.cocoabeans.commands.spigot.parsers.OfflinePlayerParser;
import net.apartium.cocoabeans.commands.spigot.parsers.PlayerParser;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;
import net.apartium.cocoabeans.spigot.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A command manager implementation for Spigot and Spigot-based platforms.
 */
public class SpigotCommandManager extends CommandManager {

    public static final Set<ArgumentParser<?>> SPIGOT_PARSERS = Set.of(
            new PlayerParser(0),
            new OfflinePlayerParser(0),
            new MaterialParser(0)
    );

    private final JavaPlugin plugin;

    /**
     * Create a new command manager instance for specified plugin
     * @param plugin plugin to own this command manager
     */
    public SpigotCommandManager(JavaPlugin plugin) {
        this(plugin, new SpigotArgumentMapper(), new SpigotExceptionArgumentMapper());
    }

    /**
     * Create a new command manager instance for specified plugin
     * @param plugin plugin to own this command manager
     * @param argumentMapper argument mapper instance
     */
    public SpigotCommandManager(JavaPlugin plugin, ArgumentMapper argumentMapper, ExceptionArgumentMapper exceptionArgumentMapper) {
        super(argumentMapper, exceptionArgumentMapper);
        this.plugin = plugin;
    }

    @Override
    protected void addCommand(CommandNode commandNode, Command command) {
        org.bukkit.command.Command cmd = new org.bukkit.command.Command(
                command.value(),
                command.description(),
                command.usage(),
                Arrays.asList(command.aliases())
        ) {
            @Override
            public boolean execute(CommandSender sender, String invoke, String[] args) {
                {
                    String[] split = invoke.split(":");
                    invoke = split[1 % split.length];
                }

                return handle(new SpigotSender<>(sender), invoke, args);
            }

            @Override
            public List<String> tabComplete(CommandSender sender, String invoke, String[] args) {
                {
                    String[] split = invoke.split(":");
                    invoke = split[1 % split.length];
                }

                return handleTabComplete(new SpigotSender<>(sender), invoke, args);
            }
        };

        Optional.ofNullable(commandNode.getClass().getAnnotation(Permission.class))
                .map(Permission::value).ifPresent(cmd::setPermission);

        Commands.getCommandMap().register(plugin.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean handle(Sender sender, String commandName, String[] args) {
        return super.handle(sender, commandName, args);
    }
}
