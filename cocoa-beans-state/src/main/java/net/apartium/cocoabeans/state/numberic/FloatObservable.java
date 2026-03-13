package net.apartium.cocoabeans.state.numberic;

import net.apartium.cocoabeans.functions.FloatSupplier;
import net.apartium.cocoabeans.state.Observable;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.AvailableSince("0.0.47")
public interface FloatObservable extends Observable<Float>, FloatSupplier {

}
