package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApiStatus.AvailableSince("0.0.30")
public class CommandInfo {

    private final List<String> descriptions = new ArrayList<>();
    private final List<String> usages = new ArrayList<>();
    private final List<String[]> longDescriptions = new ArrayList<>();

    private String description;
    private String usage;
    private String[] longDescription;

    /**
     * Get the first description
     * @return the first description
     */
    public Optional<String> getDescription() {
        if (descriptions.isEmpty())
            return Optional.empty();

        if (description == null)
            description = descriptions.get(0);

        return Optional.of(description);
    }

    /**
     * Get the first usage
     * @return the first usage
     */
    public Optional<String> getUsage() {
        if (usages.isEmpty())
            return Optional.empty();

        if (usage == null)
            usage = usages.get(0);

        return Optional.of(usage);
    }

    /**
     * Get the first long description
     * @return the first long description
     */
    public Optional<String[]> getLongDescription() {
        if (longDescriptions.isEmpty())
            return Optional.empty();

        if (longDescription == null)
            longDescription = longDescriptions.get(0);

        return Optional.of(longDescription);
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
}
