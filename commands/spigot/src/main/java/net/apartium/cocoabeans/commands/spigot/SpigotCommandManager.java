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
import net.apartium.cocoabeans.commands.lexer.CommandLexer;
import net.apartium.cocoabeans.commands.lexer.SimpleCommandLexer;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.spigot.parsers.*;
import net.apartium.cocoabeans.commands.spigot.requirements.Permission;
import net.apartium.cocoabeans.commands.virtual.VirtualCommandDefinition;
import net.apartium.cocoabeans.spigot.Commands;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A command manager implementation for Spigot and Spigot-based platforms.
 */
public class SpigotCommandManager extends CommandManager {

    public static final Set<ArgumentParser<?>> SPIGOT_PARSERS = Set.of(
            new PlayerParser(0),
            new OfflinePlayerParser(0),
            new MaterialParser(0),
            new LocationParser(0),
            new GameModeParser(0),
            new WorldParser(0),
            new PluginParser(0)
    );

    private final JavaPlugin plugin;

    /**
     * Create a new command manager instance for specified plugin
     * @param plugin plugin to own this command manager
     */
    public SpigotCommandManager(JavaPlugin plugin) {
        this(plugin, new SpigotArgumentMapper());
    }

    /**
     * Create a new command manager instance for specified plugin
     * @param plugin plugin to own this command manager
     * @param argumentMapper argument mapper instance
     */
    public SpigotCommandManager(JavaPlugin plugin, ArgumentMapper argumentMapper) {
        this(plugin, plugin.getLogger(), argumentMapper, new SimpleCommandLexer());
    }

    /**
     * Create a new command manager instance for specified plugin
     * @param plugin plugin to own this command manager
     * @param argumentMapper argument mapper instance
     * @param commandLexer command lexer instance
     * @param logger logger
     */
    @ApiStatus.AvailableSince("0.0.41")
    public SpigotCommandManager(JavaPlugin plugin, Logger logger, ArgumentMapper argumentMapper, CommandLexer commandLexer) {
        super(logger, argumentMapper, commandLexer);
        this.plugin = plugin;
    }

    @Override
    protected void addCommand(CommandNode commandNode, Command command) {
        intoBukkitCommand(
                command.value(),
                List.of(command.aliases()),
                Optional.ofNullable(commandNode.getClass().getAnnotation(Description.class)).map(Description::value).orElse(""),
                Optional.ofNullable(commandNode.getClass().getAnnotation(Usage.class)).map(Usage::value).orElse(""),
                Optional.ofNullable(commandNode.getClass().getAnnotation(Permission.class)).map(Permission::value).orElse(null)
        );
    }

    @Override
    public void addVirtualCommand(VirtualCommandDefinition definition, Function<CommandContext, Boolean> callback) {
        super.addVirtualCommand(definition, callback);
        intoBukkitCommand(
                definition.name(),
                List.copyOf(definition.aliases()),
                definition.info().getDescription().orElse(""),
                definition.info().getUsage().orElse(""),
                Optional.ofNullable(definition.metadata().get("permission"))
                        .map(Object::toString)
                        .orElse(null)
        );
    }

    private void intoBukkitCommand(String name, List<String> aliases, String description, String usage, String permission) {
        org.bukkit.command.Command cmd = new org.bukkit.command.Command(
                name,
                description,
                usage,
                aliases
        ) {
            @Override
            public boolean execute(CommandSender sender, String invoke, String[] args) {
                {
                    String[] split = invoke.split(":");
                    invoke = split[1 % split.length];
                }

                try {
                    return handle(new SpigotSender<>(sender), invoke, args);
                } catch (Throwable e) {
                    return false;
                }
            }

            @Override
            public @NotNull List<String> tabComplete(CommandSender sender, String invoke, String[] args) {
                {
                    String[] split = invoke.split(":");
                    invoke = split[1 % split.length];
                }

                return handleTabComplete(new SpigotSender<>(sender), invoke, args);
            }
        };

        if (permission != null)
            cmd.setPermission(permission);

        Commands.getCommandMap().register(plugin.getName().toLowerCase(), cmd);
    }

    @Override
    public boolean handle(Sender sender, String commandName, String[] args) throws Throwable {
        return super.handle(sender, commandName, args);
    }
}
