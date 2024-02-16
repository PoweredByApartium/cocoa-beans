/*
 * Copyright 2024 Apartium
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.apartium.cocoabeans.spigot;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.parsers.StringParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;
import net.apartium.cocoabeans.commands.spigot.SenderType;
import net.apartium.cocoabeans.commands.spigot.requirements.SenderTypeRequirement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command("test")
public class TestCommand implements CommandNode {

    @SubCommand
    @SenderTypeRequirement(SenderType.PLAYER)
    public void sayHeyToPlayer(CommandSender sender) {
        sender.sendMessage("Hey player");
    }

    @SubCommand
    @SenderTypeRequirement(SenderType.CONSOLE)
    public void sayHeyToConsole(CommandSender sender) {
        sender.sendMessage("Hey Console");
    }

    @SubCommand
    @SenderTypeRequirement(SenderType.BLOCK)
    public void sayHeyToBlock(CommandSender sender) {
        sender.sendMessage("Hey Block");
    }

    @SenderTypeRequirement(SenderType.PLAYER)
    @SubCommand(value = "0")
    public void sayZeroToPlayer(CommandSender sender) {
        sender.sendMessage("player go back to 0 when leave");
    }

    @SenderTypeRequirement(value = SenderType.PLAYER, invert = true)
    @SubCommand(value = "0")
    public void sayZeroGlobal(CommandSender sender) {
        sender.sendMessage("that is 0");
    }

    @SenderTypeRequirement(SenderType.PLAYER)
    @SubCommand(value = "ping")
    @SubCommand(value = "ping <ignore> <strings>")
    public void selfPingCheck(Player player) {
        int ping = player.getPing();
        player.sendMessage("§eYour ping is " + getPingColor(ping) + ping + "ms");
    }

    @SubCommand(value = "ping <player>")
    public void checkOtherPing(Player player, Player target) {
        int ping = target.getPing();
        player.sendMessage("§e" + target.getName() + " ping is " + getPingColor(ping) + ping + "ms");
    }

    @WithParser(value = StringParser.class, priority = -1)
    @SubCommand(value = "ping <string>")
    public void targetNotFound(Player player, String targetName) {
        player.sendMessage("§c" + targetName + " Not found!");
    }

    public void selfPingCheckWithIgnoreAndStrings(Player player) {
        selfPingCheck(player);
    }

    private String getPingColor(int ping) {
        if (ping <= 40) return "§a";
        if (ping <= 60) return "§2";
        if (ping <= 80) return "§e";
        if (ping <= 100) return "§6";
        if (ping <= 120) return "§c";
        return "§4";
    }


}
