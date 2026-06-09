package net.apartium.cocoabeans.commands;

@Command("priority-test")
public class PriorityTestCommand implements CommandNode {

    @SubCommand("keyword-a <priority>")
    public void keywordA(Sender sender, String value) {
        sender.sendMessage("keywordA: " + value);
    }

    @SubCommand("keyword-b <priority>")
    public void keywordB(Sender sender, String value) {
        sender.sendMessage("keywordB: " + value);
    }

    @SubCommand("<priority>")
    public void justParser(Sender sender, String value) {
        sender.sendMessage("justParser: " + value);
    }

    @Override
    public boolean fallbackHandle(Sender sender, String label, String[] args) {
        sender.sendMessage("fallback");
        return true;
    }
}