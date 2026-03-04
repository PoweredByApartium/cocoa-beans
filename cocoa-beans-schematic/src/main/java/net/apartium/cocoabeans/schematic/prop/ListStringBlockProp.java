package net.apartium.cocoabeans.schematic.prop;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.List;

/**
 * Represents a string list block property
 */
@ApiStatus.AvailableSince("0.0.46")
public class ListStringBlockProp implements BlockProp<List<String>> {

    protected final List<String> values;

    /**
     * Construct a new string list prop
     * @param values values
     */
    public ListStringBlockProp(List<String> values) {
        this.values = values;
    }

    @Override
    public List<String> value() {
        return Collections.unmodifiableList(values);
    }
}
