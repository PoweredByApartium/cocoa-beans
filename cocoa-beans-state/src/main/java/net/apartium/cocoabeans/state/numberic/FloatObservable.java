package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.functions.FloatSupplier;
import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

/**
 * An observable that holds a primitive {@code float} value.
 * Extends {@link Observable} with {@code Float} boxing and {@link FloatSupplier}
 * for primitive access, avoiding unnecessary boxing overhead.
 *
 * @see MutableFloatObservable
 * @see Observable#immutableNumeric(float)
 * @see Observable#mutableNumeric(float)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface FloatObservable extends Observable<Float>, FloatSupplier {

}
