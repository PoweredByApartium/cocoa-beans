package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.IntSupplier;

@ApiStatus.AvailableSince("0.0.47")
public interface IntObservable extends Observable<Integer>, IntSupplier {

}
