package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.apartium.cocoabeans.state.ImmutableObservable.EMPTY_OBSERVABLE;

/**
 * Represents a mutable state with listeners
 * @param <T> element type
 * @see ListObservable
 * @see MutableObservable
 */
@ApiStatus.AvailableSince("0.0.39")
public interface Observable<T> {

    /**
     * Return immutable observer with null value
     * @return Empty observer
     * @param <T> element type
     * Identity is guaranteed across calls to this method.
     */
    @SuppressWarnings("unchecked")
    static <T> Observable<T> empty() {
        return (Observable<T>) EMPTY_OBSERVABLE;
    }

    /**
     * Creates an immutable observable encompassing given value
     * @param value value
     * @return new observable instance
     * @param <T> element type
     */
    static <T> Observable<T> immutable(T value) {
        return new ImmutableObservable<>(value);
    }

    /**
     * Creates a mutable observable encompassing given value
     * @param value value
     * @return new observable instance
     * @param <T> element type
     */
    static <T> MutableObservable<T> mutable(T value) {
        return new MutableObservableImpl<>(value);
    }

    /**
     * Creates a new ListObservable with an empty list
     * @apiNote it would use ArrayList as is list implementation
     */
    static <E> ListObservable<E> list() {
        return new ListObservableImpl<>();
    }

    /**
     * Creates a new ListObservable with the given list
     * @param list should be modifiable
     */
    static <E> ListObservable<E> list(List<E> list) {
        return new ListObservableImpl<>(list);
    }

    /**
     * Create a new SetObservable with an empty set
     * @apiNote it would use HashSet as is set implementation
     */
    static <E> SetObservable<E> set() {
        return new SetObservableImpl<>();
    }

    /**
     * Create a new SetObservable with the given set
     * @param set should be modifiable
     */
    static <E> SetObservable<E> set(Set<E> set) {
        return new SetObservableImpl<>(set);
    }

    static <T> ObservableCompound<T> compound(Function<List<?>, T> function, List<Observable<?>> depends) {
        return new ObservableCompound<>(function, depends);
    }

    /**
     * Creates a ObservableCompound with a single argument.
     *
     * @param arg0 The state that provides the first argument.
     * @param <ARG0> The type of the first argument.
     * @return A state representing a record with a single arguments.
     */
    static <ARG0> Observable<CompoundRecords.RecordOf1<ARG0>> compound(Observable<ARG0> arg0) {
        return CompoundRecords.compound(arg0);
    }

    /**
     * Creates a ObservableCompound with two arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the second argument.
     * @param <ARG0> The type of the 1st argument.
     * @param <ARG1> The type of the 2nd argument.
     * @return A state representing a record with two arguments.
     */
    static <ARG0, ARG1> Observable<CompoundRecords.RecordOf2<ARG0, ARG1>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1) {
        return CompoundRecords.compound(arg0, arg1);
    }

    /**
     * Create a ObservableCompound with three arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @return A state representing a record with three arguments.
     */
    static <ARG0, ARG1, ARG2> Observable<CompoundRecords.RecordOf3<ARG0, ARG1, ARG2>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2) {
        return CompoundRecords.compound(arg0, arg1, arg2);
    }

    /**
     * Create a ObservableCompound with four arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @return A state representing a record with four arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3> Observable<CompoundRecords.RecordOf4<ARG0, ARG1, ARG2, ARG3>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2, Observable<ARG3> arg3) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3);
    }

    /**
     * Create a ObservableCompound with five arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param arg4 The state that provides the 5th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @param <ARG4> The type of the 5th argument.
     * @return A state representing a record with five arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3, ARG4> Observable<CompoundRecords.RecordOf5<ARG0, ARG1, ARG2, ARG3, ARG4>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2, Observable<ARG3> arg3, Observable<ARG4> arg4) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3, arg4);
    }

    /**
     * Create a ObservableCompound with six arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param arg4 The state that provides the 5th argument.
     * @param arg5 The state that provides the 6th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @param <ARG4> The type of the 5th argument.
     * @param <ARG5> The type of the 6th argument.
     * @return A state representing a record with six arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3, ARG4, ARG5> Observable<CompoundRecords.RecordOf6<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2, Observable<ARG3> arg3, Observable<ARG4> arg4,
                                                                                                                                   Observable<ARG5> arg5) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3, arg4, arg5);
    }

    /**
     * Create a ObservableCompound with seven arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param arg4 The state that provides the 5th argument.
     * @param arg5 The state that provides the 6th argument.
     * @param arg6 The state that provides the 7th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @param <ARG4> The type of the 5th argument.
     * @param <ARG5> The type of the 6th argument.
     * @param <ARG6> The type of the 7th argument.
     * @return A state representing a record with seven arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6> Observable<CompoundRecords.RecordOf7<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2, Observable<ARG3> arg3,
                                                                                                                                               Observable<ARG4> arg4, Observable<ARG5> arg5, Observable<ARG6> arg6) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    }

    /**
     * Create a ObservableCompound with eight arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param arg4 The state that provides the 5th argument.
     * @param arg5 The state that provides the 6th argument.
     * @param arg6 The state that provides the 7th argument.
     * @param arg7 The state that provides the 8th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @param <ARG4> The type of the 5th argument.
     * @param <ARG5> The type of the 6th argument.
     * @param <ARG6> The type of the 7th argument.
     * @param <ARG7> The type of the 8th argument.
     * @return A state representing a record with eight arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7> Observable<CompoundRecords.RecordOf8<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2, Observable<ARG3> arg3,
                                                                                                                                                           Observable<ARG4> arg4, Observable<ARG5> arg5, Observable<ARG6> arg6, Observable<ARG7> arg7) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }

    /**
     * Create a ObservableCompound with nine arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param arg4 The state that provides the 5th argument.
     * @param arg5 The state that provides the 6th argument.
     * @param arg6 The state that provides the 7th argument.
     * @param arg7 The state that provides the 8th argument.
     * @param arg8 The state that provides the 9th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @param <ARG4> The type of the 5th argument.
     * @param <ARG5> The type of the 6th argument.
     * @param <ARG6> The type of the 7th argument.
     * @param <ARG7> The type of the 8th argument.
     * @param <ARG8> The type of the 9th argument.
     * @return A state representing a record with nine arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7, ARG8> Observable<CompoundRecords.RecordOf9<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7, ARG8>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2,
                                                                                                                                                                       Observable<ARG3> arg3, Observable<ARG4> arg4, Observable<ARG5> arg5,
                                                                                                                                                                       Observable<ARG6> arg6, Observable<ARG7> arg7, Observable<ARG8> arg8) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }

    /**
     * Create a ObservableCompound with ten arguments.
     *
     * @param arg0 The state that provides the first argument.
     * @param arg1 The state that provides the 2nd argument.
     * @param arg2 The state that provides the 3rd argument.
     * @param arg3 The state that provides the 4th argument.
     * @param arg4 The state that provides the 5th argument.
     * @param arg5 The state that provides the 6th argument.
     * @param arg6 The state that provides the 7th argument.
     * @param arg7 The state that provides the 8th argument.
     * @param arg8 The state that provides the 9th argument.
     * @param arg9 The state that provides the 10th argument.
     * @param <ARG0> The type of the first argument.
     * @param <ARG1> The type of the 2nd argument.
     * @param <ARG2> The type of the 3rd argument.
     * @param <ARG3> The type of the 4th argument.
     * @param <ARG4> The type of the 5th argument.
     * @param <ARG5> The type of the 6th argument.
     * @param <ARG6> The type of the 7th argument.
     * @param <ARG7> The type of the 8th argument.
     * @param <ARG8> The type of the 9th argument.
     * @param <ARG9> The type of the 10th argument.
     * @return A state representing a record with ten arguments.
     */
    static <ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7, ARG8, ARG9> Observable<CompoundRecords.RecordOf10<ARG0, ARG1, ARG2, ARG3, ARG4, ARG5, ARG6, ARG7, ARG8, ARG9>> compound(Observable<ARG0> arg0, Observable<ARG1> arg1, Observable<ARG2> arg2,
                                                                                                                                                                                    Observable<ARG3> arg3, Observable<ARG4> arg4, Observable<ARG5> arg5,
                                                                                                                                                                                    Observable<ARG6> arg6, Observable<ARG7> arg7, Observable<ARG8> arg8,
                                                                                                                                                                                    Observable<ARG9> arg9) {
        return CompoundRecords.compound(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
    }

    /**
     * Will return the value of the state if it is dirty it will recompute it
     * @return value of state after recomputing if dirty otherwise the current value
     */
    T get();

    /**
     * Adds an observer to the state
     * @param observer the observer we want to add
     */
    void observe(Observer observer);

    /**
     * Maps the value of the observable using the given function when it is dirty
     * @param mapper the mapper function that will be used to map the value
     * @return new Instance of mapped observable
     * @param <M> The return type
     */
    default <M> Observable<M> map(Function<T, M> mapper) {
        return new MappedObservable<>(this, mapper);
    }

    /**
     * Create a watcher for the observable that will be attached to the given watcher manager and run the given action
     * @param watcherManager operator to attach to
     * @param consumer action to run
     * @return a new attached watcher
     */
    default AttachedWatcher<T> watch(WatcherManager watcherManager, Consumer<T> consumer) {
        return watcherManager.watch(this, consumer);
    }

}
