package net.apartium.cocoabeans.commands.parameter;

import net.apartium.cocoabeans.commands.*;

@Command("wrong-type")
public class WrongTypeParameterNames implements CommandNode {

    @SubCommand("<amount: int>")
    public void wrongType(Sender sender, @Param("amount") String amount) {
        sender.sendMessage("wrongType a " + amount);
    }

}
