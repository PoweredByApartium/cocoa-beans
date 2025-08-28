package net.apartium.cocoabeans.commands;

@Command("evil-lord")
public class ThereAnotherEvilCommand implements CommandNode {

    @SubCommand("<int>")
    public void meow(Sender sender, @Param("") int num) {
        throw new UnsupportedOperationException("how did we get here");
    }

}
