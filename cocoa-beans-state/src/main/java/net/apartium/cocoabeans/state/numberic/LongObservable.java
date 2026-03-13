package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.LongSupplier;

/**
 * An observable that holds a primitive {@code long} value.
 * Extends {@link Observable} with {@code Long} boxing and {@link LongSupplier}
 * for primitive access, avoiding unnecessary boxing overhead.
 *
 * @see MutableLongObservable
 * @see Observable#immutableNumeric(long)
 * @see Observable#mutableNumeric(long)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface LongObservable extends Observable<Long>, LongSupplier {

}
