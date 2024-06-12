package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.DummyParser;
import net.apartium.cocoabeans.commands.parsers.SourceParser;
import net.apartium.cocoabeans.commands.parsers.WithParser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@WithParser(value = DummyParser.class, priority = -1)
@Command("test-source")
public class TestSourceCommand implements CommandNode {

    private int count = 0;

    @SourceParser(keyword = "test", clazz = Test.class, resultMaxAgeInMills = -1)
    public Map<String, Test> toTest() {
        if (count++ == 1)
            throw new RuntimeException("If it reach here there is an error with max age");

        return Arrays.stream(Test.values()).
                flatMap(value -> Arrays.stream(new Map.Entry[]{
                        Map.entry(value.num + "", value),
                        Map.entry(value.name().toLowerCase(), value)
                }))
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (Test) entry.getValue()));

    }

    @SourceParser(keyword = "test2", clazz = Test.class, resultMaxAgeInMills = 10)
    public Map<String, Test> toTest2() {
        return Arrays.stream(Test.values()).
                flatMap(value -> Arrays.stream(new Map.Entry[]{
                        Map.entry(value.num + "", value),
                        Map.entry(value.name().toLowerCase(), value)
                }))
                .collect(Collectors.toMap(entry -> (String) entry.getKey(), entry -> (Test) entry.getValue()));
    }

    @SourceParser(keyword = "test3", clazz = Test.class)
    public Map<String, Test> toTest3() {
        return toTest2();
    }

    @SubCommand("<test> wow")
    public void testWow(Sender sender, Test test) {
        sender.sendMessage("testWow(Sender sender, Test test) got " + test.num);
    }

    @SubCommand("second test2 <test3>")
    @SubCommand("second test <test2>")
    @SubCommand("<test>")
    public void test(Sender sender, Test test) {
        sender.sendMessage("test(Sender sender, Test test) got " + test.num);
    }

    @SubCommand("<ignore>")
    public void testNotFound(Sender sender) {
        sender.sendMessage("testNotFound(Sender sender) didn't get test");
    }

    /* package-private */ enum Test {

        ZERO(0),
        ONE(1),
        TWO(2),
        THREE(3);

        final int num;

        Test(int num) {
            this.num = num;
        }

    }

}
