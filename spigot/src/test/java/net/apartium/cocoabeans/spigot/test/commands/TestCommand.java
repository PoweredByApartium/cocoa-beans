package net.apartium.cocoabeans.spigot.test.commands;

import net.apartium.cocoabeans.spigot.lazies.CommandAutoRegistration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAutoRegistration.Command("test")
public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return true;
    }
}
