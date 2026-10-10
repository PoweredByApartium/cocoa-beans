package net.apartium.cocoabeans.commands.option;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

@ApiStatus.AvailableSince("0.0.54")
public class OptionArgumentValue<T> {

    private final OptionArgument<T> argument;

    private final T value;

    private final String rawValue;

    public OptionArgumentValue(OptionArgument<T> argument, @Nullable T value, String rawValue) {
        this.argument = argument;
        this.value = value;
        this.rawValue = rawValue;
    }

    @Nullable
    public T orDefault() {
        if (value == null && argument.hasDefaultValue()) {
            return argument.defaultValueSupplier().get();
        } else
            return value;
    }

    @Nullable
    public T orNull() {
        return value;
    }

    @NonNull
    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    @Nullable
    public String raw() {
        return rawValue;
    }

    public boolean isSet() {
        return rawValue != null;
    }

    public OptionArgument<T> argument() {
        return argument;
    }

}
