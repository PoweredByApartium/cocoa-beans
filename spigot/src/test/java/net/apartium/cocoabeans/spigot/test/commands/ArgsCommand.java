package net.apartium.cocoabeans.spigot.test.commands;

import net.apartium.cocoabeans.spigot.lazies.CommandAutoRegistration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@CommandAutoRegistration.Command("args")
public class ArgsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length != 1)
            return false;

        return true;
    }

}
