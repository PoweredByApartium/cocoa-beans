package net.apartium.cocoabeans.commands.parameter;

import net.apartium.cocoabeans.commands.Command;
import net.apartium.cocoabeans.commands.CommandNode;
import net.apartium.cocoabeans.commands.SubCommand;

@Command("evil")
public class DuplicateParameterNameInSubCommand implements CommandNode {

    @SubCommand("<name: string> <name: string>")
    public void test(String name, String otherName) {
        // Method shouldn't be able to serialize because of the duplicate parameter name
        throw new UnsupportedOperationException("Not implemented");
    }


}
