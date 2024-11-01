package net.apartium.cocoabeans.commands;

@Command("simple")
public class SimpleCommand implements CommandNode {

    @SubCommand("<int>")
    public void amount(Sender sender, int num) {
        sender.sendMessage("Amount: " + num);
    }

    @SubCommand("<int> mhm")
    public void mhm(Sender sender, int num) {
        sender.sendMessage("Mhm: " + num);
    }

    @SubCommand("another <double>")
    public void another(Sender sender, double num) {
        sender.sendMessage("Another: " + num);
    }

}
