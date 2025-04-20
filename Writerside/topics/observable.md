# 📺 Observable api

<sup>
Available Since 0.0.39
</sup>

**Table of content:**
- [Introduction](#introduction)
- [Usage](#usage)
- [How it works](#how-it-works)
- [Advantages](#advantages)
- [What next](#what-next)

## Introduction
The `Observable<T>` interface represents a reactive, stateful object that can be watched for changes. It provides a consistent and flexible API for building reactive UIs, data flows, and dynamic logic in applications.

Observables are designed to be either immutable or mutable, with support for compound states that automatically derive values from other observables. It includes built-in support for list-based observables, mapped observables, and reactive recomposition through dependency tracking.

This system is designed to be lightweight, composable, and extensible.

## Usage
<code-block lang="java" src="common/CodeSnippets.java" include-symbol="example"/>
<code-block lang="java" src="common/CodeSnippets.java" include-symbol="listExample"/>

## How it works
```plantuml
@startuml
note as N1
    Observable is a reactive object that can be watched for changes,
    And can be used to build reactive UIs, data flows, and dynamic logic in applications
    
    Also when we say something is dirty it means that it probably needs to be re-computed
    But we talk about when it will re-compute it and when it will not re-compute it later,
    Also isDirty mean that the parent had changed and we don't just update the child immediately we wait for Observable#get to be called
    After Observable#get is being called it will make it clean again
    And if the Observable is not dirty it will not re-compute it at all
end note

interface Observer {
    + void flagAsDirty(Observable<?> observable)
}

note left of Observer::flagAsDirty
    Flag listener as dirty
end note

interface Observable<T> {
    + T get()
    --
    + void observe(Observer observer)
    --
    + <M> Observable<M> map(Function<T, M> mapper)
    --
    + AttachedWatcher<T> watch(WatcherManager watcherManager, Consumer<T> consumer)
}


note left of Observable::get
    Will return the value of the state if it is dirty it will recompute it
end note

note left of Observable::observe
    Adds an observer to the state
end note

note left of Observable::map
    Maps the value of the observable using the given function when it is dirty
end note

note left of Observable::watch
    Create a watcher for the observable that will be attached to the given watcher manager and run the given action
end note

class MappedObservable<F, T> implements Observable, Observer {
    - Observable<F> depends
    - F prev
    - boolean isDirty
    - boolean first
    - T current
    - Function<F, T> mapper
}

class ImmutableObservable<T> implements Observable {
    - T value
}

interface MutableObservable<T> extends Observable {
    - T value
    -- 
    + void set(T value)
}

note left of MutableObservable::set
    Set the value of the observable and notify listeners
end note

interface CollectionObservable<E, C extends Collection<E>> extends Observable {
    + void add(E value)
    + void remove(E value)
    + boolean addAll(Collection<? extends E> collection)
    + boolean removeAll(Collection<? extends E> collection)
    + boolean removeIf(Predicate<? super E> filter)
    + boolean retainAll(Collection<? extends E> collection)
    + void clear()
}

interface ListObservable<E> extends CollectionObservable {
    + void add(int index, E element)
    + E remove(int index)
    + void sort(Comparator<? super E> comparator);
}

interface SetObservable<E> extends CollectionObservable {
    
}

note left of SetObservable
    Set interface is for easier use and code readability
end note

class Watcher<T> implements Observer {
    - Observable<T> depends
    - Consumer<T> consumer
    - boolean first
    - boolean isDirty
    - T prevValue
    --
    + void heartbeat()
}

interface WatcherOperator {
    + void detach(AttachedWatcher<?> watcher);
}


class AttachedWatcher<T> extends Watcher {
    - WatcherOperator manager
    --
    + void attach(WatcherOperator manager)
    + boolean isAttached()
    + WatcherOperator getManager()
}

class WatcherManager implements WatcherOperator {
    + Set<Watcher<?>> watchers
    --
    + void heartbeat()
    + <T> AttachedWatcher<T> watch(Observable<T> depends, Consumer<T> consumer)
}

class ObservableCompound<T> implements Observable, Observer {
   - boolean isDirty
   - boolean first
   - Map<Observable<?>, Object> dependsOn
   - Function<List<?>, T> singularMapper;
   - T current
}

Observer *-- Observable::observe

WatcherOperator *-- AttachedWatcher::attach
AttachedWatcher *-- WatcherOperator::detach

@enduml
```

## Advantages
✅ Type-safe reactive model with strong generics and records.

♻️ Immutable and Mutable models support both predictable state and dynamic changes.

🔗 Composable—easy to derive, map, and observe values.

🧩 Extensible—can be combined into complex reactive graphs with minimal boilerplate.


🚀 Designed for performance and clarity with lazy re-computation and controlled observers.

## What next
* [](immutable-observable.md) <sub> Static observable value </sub>
* [](mutable-observable.md) <sub> Dynamic observable value </sub>
* [](compound-observable.md) <sub> Compound two or more observables </sub>
* [](mapped-observable.md) <sub> Map one Observable value to another value </sub>
* [](collection-observable.md): <sub> List, Set, Map </sub>
    * [](list-observable.md) <sub> List that could be observer </sub>
    * [](set-observable.md) <sub> Set that could be observer </sub>
* [](observer.md) <sub><a href="https://refactoring.guru/design-patterns/observer"> Observer pattern </a></sub>
* [](watcher.md) <sub> Observer any changes and run the function on heartbeat </sub>
* [Javadocs](https://cocoa-beans.apartium.net/%version%/common/net/apartium/cocoabeans/state/package-summary.html)