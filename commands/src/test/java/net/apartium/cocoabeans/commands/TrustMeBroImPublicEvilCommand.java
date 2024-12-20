package net.apartium.cocoabeans.commands;

import org.opentest4j.AssertionFailedError;

@Command("trust-me-bro")
public class TrustMeBroImPublicEvilCommand implements CommandNode {

    @SubCommand("im-public")
    private void imPublicEvilCommand(Sender sender) {
        throw new AssertionFailedError("How did we get here");
    }

}
