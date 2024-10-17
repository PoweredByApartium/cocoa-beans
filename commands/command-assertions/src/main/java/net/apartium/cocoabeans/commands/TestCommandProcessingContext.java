package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementEvaluationContext;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TestCommandProcessingContext implements CommandProcessingContext {

    private final Map<Object, List<BadCommandResponse>> reports = new HashMap<>();
    private final List<BadCommandResponse> reportsForNull = new ArrayList<>();

    private final Sender sender;
    private final String label;
    private final List<String> args;
    private final int index;

    public TestCommandProcessingContext(@NotNull Sender sender, String label, List<String> args, int index) {
        this.sender = sender;
        this.label = label;
        this.args = Collections.unmodifiableList(args);
        this.index = index;
    }

    @Override
    public @NotNull Sender sender() {
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
    public void report(Object source, @NotNull BadCommandResponse response) {
        if (source == null) {
            reportsForNull.add(response);
            return;
        }
        reports.computeIfAbsent(source, k -> new ArrayList<>())
                .add(response);
    }

    public boolean hasAnyReports() {
        return !reports.isEmpty() || !reportsForNull.isEmpty();
    }

    public List<BadCommandResponse> getReports() {
        List<BadCommandResponse> reports = new ArrayList<>();
        reports.addAll(reportsForNull);
        reports.addAll(this.reports.values().stream().flatMap(Collection::stream).toList());
        return reports;
    }

}
