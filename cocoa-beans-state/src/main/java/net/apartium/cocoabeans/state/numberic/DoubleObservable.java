package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.DoubleSupplier;

/**
 * An observable that holds a primitive {@code double} value.
 * Extends {@link Observable} with {@code Double} boxing and {@link DoubleSupplier}
 * for primitive access, avoiding unnecessary boxing overhead.
 *
 * @see MutableDoubleObservable
 * @see Observable#immutableNumeric(double)
 * @see Observable#mutableNumeric(double)
 */
@ApiStatus.AvailableSince("0.0.47")
public interface DoubleObservable extends Observable<Double>, DoubleSupplier {

}
