package net.apartium.cocoabeans.commands.option;

import net.apartium.cocoabeans.commands.parsers.ArgumentParser;
import net.apartium.cocoabeans.commands.requirements.Requirement;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NullMarked
@ApiStatus.AvailableSince("0.0.54")
public sealed interface OptionArgument<T> extends NamedArgument permits OptionArgumentImpl {

    boolean hasDefaultValue();

    ArgumentParser<T> parser();

    Supplier<T> defaultValueSupplier();

    class Builder<T> {

        /* package-private */ Builder() {

        }

        private String form;
        private Set<String> forms;
        private String usage;
        private ArgumentParser<T> parser;
        private @Nullable Supplier<T> defaultValueSupplier;
        private @Nullable Requirement requirement;

        private  @Nullable String valueHint = null;

        public Builder<T> form(String form) {
            this.form = form;
            return this;
        }

        public Builder<T> forms(Set<String> forms) {
            this.forms = forms;
            return this;
        }

        public Builder<T> forms(String... forms) {
            return forms(Set.of(forms));
        }

        public Builder<T> usage(String usage) {
            this.usage = usage;
            this.valueHint = null;
            return this;
        }

        public Builder<T> valueHint(String valueHint) {
            this.valueHint = valueHint;
            return this;
        }

        public Builder<T> parser(ArgumentParser<T> parser) {
            this.parser = parser;
            return this;
        }

        // TODO
        public Builder<T> parser(String keyword) {
            // TODO
            return this;
        }

        // TODO
        public Builder<T> validation(Predicate<T> validation) {
            // TODO
            return this;
        }

        public Builder<T> defaultValue(Supplier<T> defaultValue) {
            this.defaultValueSupplier = defaultValue;
            return this;
        }

        public Builder<T> requirement(Requirement requirement) {
            this.requirement = requirement;
            return this;
        }

        public OptionArgument<T> build() {
            if (this.valueHint != null)
                this.usage = defaultValueSupplier != null
                        ? form + " " + valueHint + "=" + defaultValueSupplier
                        : form + " " + valueHint;


            return new OptionArgumentImpl<>(
                    this.form,
                    this.forms,
                    this.usage,
                    this.parser,
                    this.defaultValueSupplier,
                    this.requirement
            );
        }

    }
}
