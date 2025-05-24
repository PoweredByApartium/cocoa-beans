package net.apartium.cocoabeans.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.*;

@ApiStatus.AvailableSince("0.0.30")
public class CommandInfo {

    private final List<String> descriptions = new ArrayList<>();
    private final List<String> usages = new ArrayList<>();
    private final List<String[]> longDescriptions = new ArrayList<>();

    /**
     * Get the first description
     * @return the first description
     */
    @JsonIgnore
    public Optional<String> getDescription() {
        if (descriptions.isEmpty())
            return Optional.empty();

        return Optional.of(descriptions.get(0));
    }

    /**
     * Get the first usage
     * @return the first usage
     */
    @JsonIgnore
    public Optional<String> getUsage() {
        if (usages.isEmpty())
            return Optional.empty();

        return Optional.of(usages.get(0));
    }

    /**
     * Get the first long description
     * @return the first long description
     */
    @JsonIgnore
    public Optional<String[]> getLongDescription() {
        if (longDescriptions.isEmpty())
            return Optional.empty();

        return Optional.of(longDescriptions.get(0));
    }

    /**
     * Get all descriptions
     * @return all descriptions
     */
    public List<String> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

    /**
     * Get all usages
     * @return all usages
     */
    public List<String> getUsages() {
        return Collections.unmodifiableList(usages);
    }

    /**
     * Get all long descriptions
     * @return all long descriptions
     */
    public List<String[]> getLongDescriptions() {
        return Collections.unmodifiableList(longDescriptions);
    }

    /* package-private */ void addDescription(final Description description, boolean first) {
        if (first) {
            descriptions.add(0, description.value());
            return;
        }

        descriptions.add(description.value());
    }

    /* package-private */ void addUsage(final Usage usage, boolean first) {
        if (first) {
            usages.add(0, usage.value());
            return;
        }

        usages.add(usage.value());
    }

    /* package-private */ void addLongDescription(final LongDescription longDescription, boolean first) {
        if (first) {
            longDescriptions.add(0, longDescription.value());
            return;
        }

        longDescriptions.add(longDescription.value());
    }

    /* package-private */ void fromAnnotations(Annotation[] annotations, boolean first) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof Description description) {
                addDescription(description, first);
                continue;
            }

            if (annotation instanceof Usage usage) {
                addUsage(usage, first);
                continue;
            }

            if (annotation instanceof LongDescription longDescription) {
                addLongDescription(longDescription, first);
                continue;
            }
        }
    }

    /* package-private */ void fromCommandInfo(CommandInfo other) {
        descriptions.addAll(other.descriptions);
        usages.addAll(other.usages);
        longDescriptions.addAll(other.longDescriptions);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CommandInfo that = (CommandInfo) o;
        return Objects.equals(descriptions, that.descriptions) && Objects.equals(usages, that.usages) && Objects.equals(longDescriptions, that.longDescriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descriptions, usages, longDescriptions);
    }

    @JsonIgnore
    public static CommandInfo createFromAnnotations(Collection<Annotation[]> collection) {
        CommandInfo info = new CommandInfo();
        for (Annotation[] annotations : collection)
            info.fromAnnotations(annotations, false);

        return info;
    }
}
