package net.apartium.cocoabeans.commands;

@Description("This command showcase command info")
@LongDescription({
        "Lorem ipsum dolor sit amet. Quo esse distinctio ea nihil pariatur non",
        "obcaecati sapiente eos quidem magnam. Et quod dolor rem magni",
        "voluptatibus in pariatur vitae. Et quis velit ut ducimus ipsam et obcaecati",
        "consequatur et velit accusantium vel similique consequatur et rerum",
        "eaque in quidem molestiae."
})
@Usage("/info")
@Command("info")
public class CommandInfoForTest implements CommandNode {

    @Description("meow test very cool")
    @Usage("just run it")
    @SubCommand
    public void info(Sender sender, CommandContext context) {
        sender.sendMessage("Description: " + context.commandInfo().getDescription().orElse(null));
        sender.sendMessage("Usage: " + context.commandInfo().getUsage().orElse(null));
    }

}
