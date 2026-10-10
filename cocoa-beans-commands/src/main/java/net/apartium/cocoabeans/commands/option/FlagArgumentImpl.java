package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

@NullMarked
/* package-private */ record FlagArgumentImpl(
        String form,
        Set<String> forms,
        @Nullable Requirement requirement
) implements FlagArgument {

    FlagArgumentImpl {
        Ensures.notEmpty(form, "Form cannot be empty");
        forms = Set.copyOf(forms);
    }

    FlagArgumentImpl(String form, Collection<String> forms) {
        this(form, forms, null);
    }

    FlagArgumentImpl(String form, Collection<String> forms, @Nullable Requirement requirement) {
        this(form, Set.copyOf(forms), requirement);
    }

    @Override
    public String usage() {
        return form();
    }
}
