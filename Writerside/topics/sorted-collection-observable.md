# 🔢 Sorted Collection Observable

<sup>
Available Since 0.0.50
</sup>

## Introduction
`sorted` is a **per-element, live** ordering operator on `ListObservable`.
Each element of the source is mapped to an `Observable<T>` **sort key**, and the derived
list is kept sorted by those keys using a `Comparator`.

Use this when the items in your list expose an **observable property** you want to
order by — e.g. a player's score, an entity's distance from spawn, or a task's
priority — and you want the order to update automatically when those properties change.

```java
ListObservable<DisplayPlayer> players = Observable.list();

ListObservable<DisplayPlayer> alphabetical = players.sorted(
        DisplayPlayer::displayName,
        Comparator.naturalOrder()
);
// re-orders when:
//   - players are added/removed
//   - any tracked player's displayName changes
```

## Overloads

`ListObservable<E>` exposes two overloads:

```java
<T> ListObservable<E> sorted(Function<E, Observable<T>> mapper, Comparator<? super T> comparator);
<T> ListObservable<E> sorted(Function<E, Observable<T>> mapper, Observable<Comparator<? super T>> comparator);
```

| Overload                                 | Use when                                                              |
|------------------------------------------|------------------------------------------------------------------------|
| `Comparator<? super T>`                  | The ordering rule is fixed for the lifetime of the view                |
| `Observable<Comparator<? super T>>`      | You want to **swap the ordering rule at runtime** (e.g. asc ↔ desc)    |

The static-comparator overload is a thin default that wraps your comparator in
`Observable.immutable(...)` and delegates to the observable form — both share the
same underlying implementation.

## Usage

### Static comparator
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="sortedSample"/>

### Observable comparator
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="sortedObservableComparatorSample"/>

## Behavior
- Subscribes to **each element's key observable** while the element is present in the source.
- When an element leaves the source, its inner subscription is released.
- Multiple elements may share the same key observable — it is observed **once**
  and stays observed as long as **any** referencing element is still in the source.
- The mapper is invoked at most once per element per source change, **not** on every key update.
- **Comparator observable** is observed too: changes propagate just like source and
  key changes. Re-setting the **same** comparator reference is a no-op (identity check).
- Cached: `get()` reuses the previous result until the source, a tracked key, or the
  comparator changes.
- **Short-circuit**: before re-sorting, the implementation checks whether the
  current source order is already sorted by the current keys. If so, `List.sort`
  is skipped entirely — only an O(n) scan runs.
- Sort is **stable**: equal keys preserve the source's relative order.

## Notes

- The derived list is **read-only**. `add`, `remove`, `addAll`, `removeAll`,
  `removeIf`, `retainAll`, `clear` — and the list-specific `add(int, E)`,
  `remove(int)`, `sort` — all throw `UnsupportedOperationException`.
- A `null` mapper result is allowed and means "this element has no key". Pair it
  with a null-tolerant comparator (e.g. `Comparator.nullsFirst(...)`) if you need to
  handle that case.
- Duplicates in the source list are preserved — each occurrence appears in the
  sorted result, with stability across equal keys.

## Advantages
- 🔢 **Live ordering**: order tracks collection changes, per-element key changes, **and** comparator changes
- 🔄 **Swappable ordering**: with the observable-comparator overload, flip asc/desc (or any rule) without touching the source
- ⚡ **Cheap when stable**: already-sorted inputs short-circuit and skip `List.sort`
- 🔒 **No observer leaks**: subscriptions are released when elements leave
- 🤝 **Sharing-friendly**: elements sharing a key observable share the subscription
- ⛓ **Composable**: chains naturally with `filter`, `mapEach`, and `flatMapEach`
