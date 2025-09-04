package net.apartium.cocoabeans.commands.mapper;

import net.apartium.cocoabeans.commands.*;

import java.util.UUID;

@Command("cool")
public class MyCoolCommand implements CommandNode {

    @SubCommand("a <uuid>")
    public void subCommand(Sender sender, String uuidAsString) {
        sender.sendMessage("a: " + uuidAsString);
    }

    @SubCommand("b <uuid>")
    public void subCommand(Sender sender, UUID uuid) {
        sender.sendMessage("b: " + uuid);
    }

    @SubCommand("c <string> <uuid>")
    public void subCommandC(Sender sender, String myString, String uuidAsString) {
        sender.sendMessage(myString + " -c uuid: " + uuidAsString);
    }

    @SubCommand("d <uuid> <string>")
    public void subCommandD(Sender sender, String myString, String uuidAsString) {
        sender.sendMessage(myString + " -d uuid: " + uuidAsString);
    }

    @SubCommand("e <string> <uuid>")
    public void subCommand(Sender sender, String myString, UUID uuid) {
        sender.sendMessage(myString + " -e uuid: " + uuid);
    }

    @SubCommand("f <my-uuid: uuid> <string>")
    public void subCommandF(Sender sender, @Param("my-uuid") String uuidAsString, String myString) {
        sender.sendMessage(myString + " -f uuid: " + uuidAsString);
    }


    @SubCommand("g <my-uuid: uuid> <string>")
    public void subCommandG(Sender sender, String myString, @Param("my-uuid") String uuidAsString) {
        sender.sendMessage(myString + " -g uuid: " + uuidAsString);
    }
}
