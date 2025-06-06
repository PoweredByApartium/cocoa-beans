# 📋 Mutable Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction

`Observable#mutable` returns a reactive container that allows both reading and updating its internal value.
It implements the `Observable<T>` interface and provides a `set(T)` method to modify the current state.

Mutable observers are the foundation for building dynamic, reactive state in your application. 

Any change to the state triggers notifications to dependent observers and re-computes them as needed.

## Usage
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="mutable"/>
You can use it directly, or combine it with mapping, watching, or composing multiple observables.

## How it works
A `MutableObservable` keeps an internal value and a list of dependent observers. When `set()` is called:
 **1**. The internal value is updated.

 **2**. All observers are notified and may trigger their effects via `Observer#flagAsDirty`

If the new value is equal to the old one (by `Object#equals`), dependents will not be notified, thereby improving performance.

## Advantages

- 📝 **Mutable state**: Ideal for reactive inputs, counters, toggles, and form fields.
- 🔔 **Notifies observers**: Automatically propagates changes through the system.
- 🔗 **Composable**: Works with mapping, compound logics and other observables.
- ⚡ **Efficient**: No re-computation or dirty state.

## Related topics

* [](immutable-observable.md)
* [](mapped-observable.md)
* [](compound-observable.md)