package net.apartium.cocoabeans.commands;

@Command("lol")
public class UsingBaseCommandTest extends BaseCommandTest {

    @Override
    public void runTest(Sender sender) {
        sender.sendMessage("runTest: for lol");
    }

    @SubCommand("testing <test>")
    public void test(Sender sender, String test) {
        sender.sendMessage("test: " + test);
    }

}
