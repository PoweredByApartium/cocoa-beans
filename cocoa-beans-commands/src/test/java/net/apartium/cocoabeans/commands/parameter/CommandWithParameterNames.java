package net.apartium.cocoabeans.commands.parameter;

import net.apartium.cocoabeans.commands.*;
import net.apartium.cocoabeans.commands.parsers.SourceParser;

import java.util.Map;

@Command("test")
public class CommandWithParameterNames implements CommandNode {

    @SubCommand("give <string> <int> <amount: int>")
    public void amount(Sender sender, @Param("amount") int amount, String target, int num) {
        sender.sendMessage(target + " amount is " + amount + " and num: " + num);
    }

    @SubCommand("test <target: string> <num: int> <amount: int>")
    public void amount2(Sender sender, @Param("amount") int amount, @Param("target") String target,  @Param("num") int num) {
        sender.sendMessage(target + " amount2 is " + amount + " and num: " + num);
    }

    @SubCommand("mhm <target: string> <num: int> <amount: int> <string> <string> <int> <string> <int> <int>")
    public void mhm(Sender sender, int a, int b, int c, String strA, String strB, String strC, @Param("amount") int amount, @Param("target") String target,  @Param("num") int num) {
        sender.sendMessage(
                "mhmm{" +
                        "a=" + a + ", " +
                        "b=" + b + ", " +
                        "c=" + c + ", " +
                        "strA=" + strA + ", " +
                        "strB=" + strB + ", " +
                        "strC=" + strC + ", " +
                        "amount=" + amount + ", " +
                        "target=" + target + ", " +
                        "num=" + num +
                        "}"
        );
    }

    @SubCommand("testing <int> <amount: string> <string>")
    public void testing(Sender sender, int num, String amount, String str) {
        sender.sendMessage("testing a " + str + " with number of " + num + " by the amount of " + amount);
    }

    @SubCommand("send <target: player> <strings>")
    public void send(@Param("target") Sender target, Sender sender, String message) {
        sender.sendMessage("sending message to " + ((PlayerSender) target).getName() + ": " + message);
    }

    @SourceParser(keyword = "player", clazz= PlayerSender.class, resultMaxAgeInMills = -1, ignoreCase = true)
    public Map<String, PlayerSender> playerSenderMap() {
        return Map.of(
                "ikfir", new PlayerSender("ikfir"),
                "jeff", new PlayerSender("jeff")
        );
    }

}
