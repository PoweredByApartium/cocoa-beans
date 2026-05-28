package net.apartium.cocoabeans.commands;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.*;

@ApiStatus.AvailableSince("0.0.30")
public record CommandInfo(
        List<String> descriptions,
        List<String> usages,
        List<List<String>> longDescriptions
) {

    public CommandInfo(
            List<String> descriptions,
            List<String> usages,
            List<List<String>> longDescriptions
    ) {
        this.descriptions = List.copyOf(descriptions);
        this.usages = List.copyOf(usages);
        this.longDescriptions = List.copyOf(longDescriptions);
    }

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
    public Optional<List<String>> getLongDescription() {
        if (longDescriptions.isEmpty())
            return Optional.empty();

        return Optional.of(longDescriptions.get(0));
    }

    /**
     * Get all descriptions
     * @return all descriptions
     */
    public List<String> getDescriptions() {
        return descriptions;
    }

    /**
     * Get all usages
     * @return all usages
     */
    public List<String> getUsages() {
        return usages;
    }

    /**
     * Get all long descriptions
     * @return all long descriptions
     */
    public List<List<String>> getLongDescriptions() {
        return longDescriptions;
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
        List<String> descriptions = new ArrayList<>();
        List<String> usages = new ArrayList<>();
        List<List<String>> longDescriptions = new ArrayList<>();

        for (Annotation[] annotations : collection) {
            for (Annotation annotation : annotations) {
                if (annotation instanceof Description description) {
                    descriptions.add(description.value());
                    continue;
                }

                if (annotation instanceof Usage usage) {
                    usages.add(usage.value());
                    continue;
                }

                if (annotation instanceof LongDescription longDescription)
                    longDescriptions.add(Arrays.asList(longDescription.value()));
            }
        }

        return new CommandInfo(descriptions, usages, longDescriptions);
    }

    private static <T> List<T> combine(List<T> a, List<T> b) {
        List<T> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }

    public CommandInfo merge(CommandInfo other) {
        return new CommandInfo(
                combine(descriptions, other.descriptions),
                combine(usages, other.usages),
                combine(longDescriptions, other.longDescriptions)
        );
    }
}
