package net.apartium.cocoabeans.commands;

@Command("the-devil")
public class MoreEvilCommand implements CommandNode {

    @SubCommand("lol")
    public void lol(Sender sender, double number) {
        sender.sendMessage("§c" + number);
    }

}
