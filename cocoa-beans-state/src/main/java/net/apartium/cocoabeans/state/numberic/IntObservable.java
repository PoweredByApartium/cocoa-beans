package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntSupplier;

/**
 * An observable that holds a primitive {@code int} value.
 * Extends {@link Observable} with {@code Integer} boxing and {@link IntSupplier}
 * for primitive access, avoiding unnecessary boxing overhead.
 *
 * @see MutableIntObservable
 * @see Observable#immutableNumeric(int)
 * @see Observable#mutableNumeric(int)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface IntObservable extends Observable<Integer>, IntSupplier {

}
