package net.apartium.cocoabeans.commands.requirements;

import net.apartium.cocoabeans.commands.exception.BadCommandResponse;
import org.jetbrains.annotations.ApiStatus;

/**
 * Represents the result of a requirement check
 * This class encapsulates whether a requirement has been met, and optionally provides an error response and additional values if the requirement was met
 */
public class RequirementResult {

    private final BadCommandResponse error;
    private final boolean meetRequirement;
    private final Value[] values;

    /**
     * Constructs a new {@code RequirementResult} with the specified error, requirement status, and optional values.
     *
     * @param error the error response, or {@code null} if no error
     * @param meetRequirement {@code true} if the requirement is met, {@code false} otherwise
     * @param values optional values associated with the requirement result (like @InGame which game is it etc...)
     */
    private RequirementResult(BadCommandResponse error, boolean meetRequirement, Value... values) {
        this.error = error;
        this.meetRequirement = meetRequirement;
        this.values = values;
    }

    /**
     * Returns the error response associated with this result.
     *
     * @return the error response, or {@code null} if no error
     */
    public BadCommandResponse getError() {
        return error;
    }

    /**
     * Checks whether there is an error associated with this result.
     *
     * @return {@code true} if there is an error, {@code false} otherwise
     */
    public boolean hasError() {
        return error != null;
    }

    /**
     * Indicates whether the requirement has been met.
     *
     * @return {@code true} if the requirement is met, {@code false} otherwise
     */
    public boolean meetRequirement() {
        return meetRequirement;
    }

    /**
     * Returns the optional values associated with this result.
     * @return optional values associated with this result
     */
    public Value[] getValues() {
        return values;
    }

    /**
     * Creates a {@code RequirementResult} representing an error condition.
     *
     * @param error the error response to be associated with this result
     * @return a new {@code RequirementResult} instance with the specified error
     */
    public static RequirementResult error(BadCommandResponse error) {
        return new RequirementResult(error, false);
    }

    /**
     * Creates a {@code RequirementResult} indicating that the requirement has been met without additional values.
     *
     * @return a new {@code RequirementResult} instance indicating the requirement is met
     */
    public static RequirementResult meet() {
        return new RequirementResult(null, true);
    }

    /**
     * Creates a {@code RequirementResult} indicating that the requirement has been met with optional values.
     *
     * @param values optional values associated with the result
     * @return a new {@code RequirementResult} instance indicating the requirement is met with the specified values
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static RequirementResult meet(Value... values) {
        return new RequirementResult(null, true, values);
    }

    /**
     * Creates a {@code Value} from the specified value and class.
     *
     * @param value the value
     * @param clazz the class of the value
     * @return a new {@code Value} instance with the specified value and class type
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static Value valueOf(Object value, Class<?> clazz) {
        return new Value(value, clazz);
    }

    /**
     * Creates a {@code Value} from the specified value.
     *
     * @param value the value
     * @return a new {@code Value} instance with Object#getClass as class type
     */
    @ApiStatus.AvailableSince("0.0.30")
    public static Value valueOf(Object value) {
        return new Value(value, value.getClass());
    }

    /**
     * Represents a value associated with a requirement result.
     *
     * @param value the value
     * @param clazz the class of the value
     */
    @ApiStatus.AvailableSince("0.0.30")
    public record Value(Object value, Class<?> clazz) {}

}
