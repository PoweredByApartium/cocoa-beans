package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;

import java.util.*;

public class TestCommandProcessingContext implements CommandProcessingContext {

    private final List<BadCommandResponse> reports = new ArrayList<>();

    private final Sender sender;
    private final String label;
    private final List<String> args;
    private final int index;

    public TestCommandProcessingContext(Sender sender, String label, List<String> args, int index) {
        this.sender = sender;
        this.label = label;
        this.args = Collections.unmodifiableList(args);
        this.index = index;
    }

    @Override
    public Sender sender() {
        return sender;
    }

    @Override
    public String label() {
        return label;
    }

    @Override
    public List<String> args() {
        return args;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public RequirementResult senderMeetsRequirement(Requirement requirement) {
        return requirement.meetsRequirement(new RequirementEvaluationContext(sender, label, args.toArray(new String[0]), index));
    }

    @Override
    public void report(Object source, BadCommandResponse response) {
        reports.add(response);
    }

    public boolean hasAnyReports() {
        return !reports.isEmpty();
    }

    public List<BadCommandResponse> getReports() {
        return Collections.unmodifiableList(reports);
    }

}
