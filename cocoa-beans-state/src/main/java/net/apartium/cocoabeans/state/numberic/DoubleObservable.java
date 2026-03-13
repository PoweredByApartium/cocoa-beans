package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.DoubleSupplier;

@ApiStatus.AvailableSince("0.0.47")
public interface DoubleObservable extends Observable<Double>, DoubleSupplier {

}
