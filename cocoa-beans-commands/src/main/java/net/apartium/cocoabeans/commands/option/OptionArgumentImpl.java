package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

@NullMarked
@ApiStatus.AvailableSince("0.0.54")
/* package-private */ record OptionArgumentImpl<T>(
        String form,
        Set<String> forms,
        String usage,
        ArgumentParser<T> parser,
        @Nullable Supplier<T> defaultValueSupplier,
        @Nullable Requirement requirement
) implements OptionArgument<T> {

    OptionArgumentImpl {
        Ensures.notEmpty(form, "Form cannot be empty");
        Ensures.notEmpty(usage, "Usage cannot be empty");
        Ensures.notNull(parser, "Parser cannot be null");

        forms = Set.copyOf(forms);
    }

    @Override
    public boolean hasDefaultValue() {
        return defaultValueSupplier != null;
    }


}
