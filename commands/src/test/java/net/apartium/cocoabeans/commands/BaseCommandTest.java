package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.parsers.SourceParser;

import java.util.Map;

public abstract class BaseCommandTest implements CommandNode {

    @SourceParser(keyword = "test", clazz = String.class, resultMaxAgeInMills = -1)
    public Map<String, String> toTest() {
        return Map.of(
                "key0", "value0",
                "key1", "value1",
                "key2", "value2",
                "key3", "value3",
                "key4", "value4",
                "key5", "value5",
                "key6", "value6",
                "key7", "value7",
                "key8", "value8",
                "key9", "value9"
        );
    }

    @SubCommand("test")
    public abstract void runTest(Sender sender);

}
