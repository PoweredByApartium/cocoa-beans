package net.apartium.cocoabeans.commands;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ApiStatus.AvailableSince("0.0.30")
public class CommandInfo {

    private final List<Description> descriptions = new ArrayList<>();
    private final List<Usage> usages = new ArrayList<>();
    private final List<LongDescription> longDescriptions = new ArrayList<>();

    private Description description;
    private Usage usage;
    private LongDescription longDescription;

    public Optional<Description> getDescription() {
        if (descriptions.isEmpty())
            return Optional.empty();

        if (description == null)
            description = descriptions.get(0);

        return Optional.of(description);
    }

    public Optional<Usage> getUsage() {
        if (usages.isEmpty())
            return Optional.empty();

        if (usage == null)
            usage = usages.get(0);

        return Optional.of(usage);
    }

    public Optional<LongDescription> getLongDescription() {
        if (longDescriptions.isEmpty())
            return Optional.empty();

        if (longDescription == null)
            longDescription = longDescriptions.get(0);

        return Optional.of(longDescription);
    }

    public List<Description> getDescriptions() {
        return Collections.unmodifiableList(descriptions);
    }

    public List<Usage> getUsages() {
        return Collections.unmodifiableList(usages);
    }

    public List<LongDescription> getLongDescriptions() {
        return Collections.unmodifiableList(longDescriptions);
    }

    public void addDescription(final Description description, boolean first) {
        if (first) {
            descriptions.add(0, description);
            return;
        }

        descriptions.add(description);
    }

    public void addUsage(final Usage usage, boolean first) {
        if (first) {
            usages.add(0, usage);
            return;
        }

        usages.add(usage);
    }

    public void addLongDescription(final LongDescription longDescription, boolean first) {
        if (first) {
            longDescriptions.add(0, longDescription);
            return;
        }

        longDescriptions.add(longDescription);
    }

    public void serialize(Annotation[] annotations, boolean first) {
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
}
