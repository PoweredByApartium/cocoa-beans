package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@ApiStatus.AvailableSince("0.0.54")
@NullMarked
public sealed interface NamedArgument permits FlagArgument, OptionArgument {

    static <T> OptionArgument.Builder<T> option() {
        return new OptionArgument.Builder<>();
    }

    static FlagArgument flag(String form, Collection<String> forms) {
        return new FlagArgumentImpl(form, forms);
    }

    static FlagArgument flag(String form, String... forms) {
        return flag(form, Arrays.asList(forms));
    }

    String form();

    Set<String> forms();

    String usage();

    @Nullable Requirement requirement();

}
