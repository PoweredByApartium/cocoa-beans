package net.apartium.cocoabeans.commands;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import net.apartium.cocoabeans.commands.requirements.RequirementResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.AvailableSince("0.0.54")
public record IndexedCommandProcessingContext(
        CommandProcessingContext context,
        int index,
        @Nullable Map<Object, BadCommandResponse> errors
) implements CommandProcessingContext {

    public IndexedCommandProcessingContext(CommandProcessingContext context, int index, @Nullable Map<Object, BadCommandResponse> errors) {
        if (context instanceof IndexedCommandProcessingContext indexedContext)
            context = indexedContext.context();

        this.context = context;
        this.index = index;
        this.errors = errors != null ? new IdentityHashMap<>(errors) : null;
    }

    public IndexedCommandProcessingContext(CommandProcessingContext context, int index) {
        this(context, index, false);
    }

    public IndexedCommandProcessingContext(CommandProcessingContext context, int index, boolean captureReports) {
        this(context, index, captureReports ? Map.of() : null);
    }


    @Override
    public @NotNull Sender sender() {
        return context.sender();
    }

    @Override
    public String label() {
        return context.label();
    }

    @Override
    public List<String> args() {
        return context.args();
    }

    @Override
    public RequirementResult senderMeetsRequirement(Requirement requirement) {
        return context.senderMeetsRequirement(requirement);
    }

    @Override
    public void report(Object source, @NotNull BadCommandResponse response) {
        if (errors != null) errors.put(source, response);
        else context.report(source, response);
    }

}
