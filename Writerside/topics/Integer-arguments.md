# 🧮️ Integer arguments

```java
@Command("amount")
public class AmountCommand implements CommandNode {

    // called if one param is provided, but it's not a number
    @WithParser(value = DummyParser.class, priority = -1)
    @SubCommand("<ignore>")
    public void invalidNumber(CommandSender sender) {
        sender.sendMessage("§cInvalid number!");
    }

    // called when a single numerical arg is provided
    @SubCommand("<int>")
    public void amount(CommandSender sender, int num) {
        if (num <= 0) {
            sender.sendMessage("§cNumber must be bigger than zero");
            return;
        }

        int stacks = num / 64;
        int remaining = num % 64;

        StringBuilder sb = new StringBuilder();
        if (stacks != 0) sb.append("§c").append(stacks).append(" §estack").append(stacks != 1 ? "s " : " ");
        if (remaining != 0) sb.append("§c").append(remaining).append(" ");


        sender.sendMessage("§c" + num + " §eis " + sb.substring(0, sb.length() - 1));
    }

    // called when more than one argument is provided
    @SubCommand
    @SubCommand("<strings>")
    public void usage(CommandSender sender, CommandContext context) {
        sender.sendMessage("§cUsage: /" + context.commandName() + " <amount>");
    }

}
```