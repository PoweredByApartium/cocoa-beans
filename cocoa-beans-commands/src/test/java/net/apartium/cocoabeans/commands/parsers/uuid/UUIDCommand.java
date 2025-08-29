package net.apartium.cocoabeans.commands.parsers.uuid;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.Sender;
import net.apartium.cocoabeans.commands.SubCommand;
import net.apartium.cocoabeans.commands.exception.ExceptionHandle;
import net.apartium.cocoabeans.commands.parsers.exception.InvalidUUIDResponse;

import java.util.UUID;

@Command("uuid")
public class UUIDCommand implements CommandNode {

    @SubCommand("<uuid>")
    public void uuid(Sender sender, UUID uuid) {
        sender.sendMessage("Got UUID: " + uuid.toString());
    }

    @ExceptionHandle(InvalidUUIDResponse.InvalidUUIDException.class)
    public void invalidUUID(Sender sender, InvalidUUIDResponse.InvalidUUIDException response) {
        sender.sendMessage("Invalid UUID: " + response.getUserInput());
    }

}
