package net.apartium.cocoabeans.state;

/* package-private */ class ImmutableObservable<T> implements Observable<T> {

    /* package-private */ static final Observable<?> EMPTY_OBSERVABLE = new ImmutableObservable<>(null);


    private final T value;

    public ImmutableObservable(T value) {
        this.value = value;
    }

    /**
     * Will return the value of the state if it is dirty it will recompute it
     *
     * @return value of state after recomputing if dirty otherwise the current value
     */
    @Override
    public T get() {
        return value;
    }

    /**
     * Add a observer to the state
     *
     * @param observer the observer we want to add
     */
    @Override
    public void observe(Observer observer) {
        // ignored
    }
}
