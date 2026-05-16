# 🔄 Collection Observable Conversion

<sup>
Available Since 0.0.50
</sup>

## Introduction
`as` is a conversion operator on `CollectionObservable` that transforms a collection observable
into a **different collection type** using a `ObservableCollectionType`.

For example, you can derive a `SetObservable` from a `ListObservable` — duplicates are
automatically collapsed, and the result stays in sync with the source:

```java
ListObservable<Integer> numbers = Observable.list();
numbers.addAll(List.of(1, 2, 2, 3));

SetObservable<Integer> uniqueNumbers = numbers.as(ObservableCollectionType.toSet());
// Set.of(1, 2, 3)
```

The `ObservableCollectionType` utility class provides ready-made collectors for common conversions.

## Usage
<tabs>
    <tab title="List to Set">
        <code-block lang="java" src="state/CodeSnippets.java" include-symbol="listToSetConversion"/>
    </tab>
    <tab title="List to List (derived copy)">
        <code-block lang="java" src="state/CodeSnippets.java" include-symbol="listToListConversion"/>
    </tab>
</tabs>

## Built-in collectors

| Method                              | Source                        | Result              | Default snapshot        | Default collection   |
|-------------------------------------|-------------------------------|---------------------|-------------------------|----------------------|
| `ObservableCollectionType.toSet()`      | any `CollectionObservable<E>` | `SetObservable<E>`  | `Set::copyOf`           | `HashSet::new`       |
| `ObservableCollectionType.toList()`     | any `CollectionObservable<E>` | `ListObservable<E>` | `List::copyOf`          | `ArrayList::new`     |

Both methods also accept **custom factory functions** for the snapshot and mutable-collection
creation, so you can use a `LinkedHashSet`, `CopyOnWriteArrayList`, or any other implementation:

```java
SetObservable<Integer> ordered = base.as(
    ObservableCollectionType.toSet(LinkedHashSet::new, LinkedHashSet::new)
);
```

## Behavior
- The derived collection is **read-only** — `add`, `remove`, `addAll`, `removeAll`,
  `removeIf`, `retainAll`, `clear` all throw `UnsupportedOperationException`.
- Snapshots returned by `get()` are **immutable** and **cached** — the snapshot is only
  recomputed when the source collection changes.
- Previous snapshots are **not affected** by later source changes; they remain a frozen view
  of the collection at the time they were obtained.
- Derived collections support **chaining** with other operators like `mapEach`, `flatMapEach`,
  and `filter`.

## Advantages
- 🔄 **Live conversion**: always reflects the latest state of the source collection
- 🧊 **Stable snapshots**: each `get()` result is immutable and won't change under you
- 🧱 **Composable**: the result is a full `CollectionObservable` — chain `filter`, `mapEach`, etc.
- 🔌 **Extensible**: implement your own `ObservableCollectionType` for custom collection types
- 🪶 **Lazy**: conversion only happens when `get()` is called and the source has changed
