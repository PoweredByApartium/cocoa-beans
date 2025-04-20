# 🗿 Immutable Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction

`ImmutableObservable<T>` is a simple implementation of the `Observable<T>` interface that always returns a fixed value.
It never becomes dirty and does not trigger re-computation or notify observers. 
This makes it ideal for constant values within a reactive system, naturally bridging the gap between the two.

## Usage
<code-block lang="java" src="common/CodeSnippets.java" include-symbol="immutable"/>

You can also use immutable observables as part of [mapped](mapped-observable.md) or [compound](compound-observable.md) observables to combine constants with dynamic values.

## How it works

An `ImmutableObservable` holds a single value and implements the `Observable<T>` interface.
Since the value never changes,
calling `get()` will always return the same result without marking it as dirty or notifying any observers.

It effectively acts as a no-op in the dependency graph—useful for simplifying logic where a value is static, but uniformity with other observables is desired.

This is functionally similar to using a [mutable](mutable-observable.md) observable value but not changing its value, with some performance benefits.

## Advantages
- ✔️ **Zero overhead**: No re-computation, no dirty state.
- 🔒 **Safe & predictable**: Guaranteed to never change.
- 🧱 **Composable**: Works seamlessly with mapped and compound observables.
- 🛠️ **Ideal for constants**: Use to represent fixed values in reactive trees.

## Related topics
* [](mapped-observable.md)
* [](compound-observable.md)
* [](mutable-observable.md)