# 🪐 Flat Mapping Collection Observable

<sup>
Available Since 0.0.50
</sup>

## Introduction
`flatMapEach` is the per-element analogue of [](flat-map-observable.md) for collections.
Each element of the source is mapped to an `Observable<R>`, and the derived collection
contains the **current value of each element's observable**.

Use this when the items in your collection expose **observable properties** —
e.g. a player's display name or an entity's health — and you want a live view of
one of those properties across the whole collection.

```java
ListObservable<DisplayPlayer> players = Observable.list();

ListObservable<String> displayNames = players.flatMapEach(DisplayPlayer::displayName);
// updates when:
//   - players are added/removed
//   - any tracked player's displayName changes
```

## Usage
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="flatMapEachSample"/>

## Behavior
- Subscribes to **each element's inner observable** while the element is present in the source.
- When an element leaves the source, its inner subscription is released.
- Multiple elements may share the same inner observable — it is observed **once**
  and stays observed as long as **any** referencing element is still in the source.
- The mapper is invoked at most once per element per source change, **not** on every inner-value update.
- Cached: `get()` reuses the previous result until the source changes or a tracked inner observable changes.

## Notes per collection type
| Source                  | Result                  | Element duplication                                                                 |
|-------------------------|-------------------------|--------------------------------------------------------------------------------------|
| `ListObservable<E>`     | `ListObservable<R>`     | Order and **multiplicity preserved** — each occurrence resolves to its inner observable's current value |
| `SetObservable<E>`      | `SetObservable<R>`      | The source already deduplicates by element                                           |

> **Subscription bookkeeping is deduped.** Even when the same element appears
> several times in a list, its inner observable is subscribed to **once**. A
> single change to that inner observable then updates every occurrence of the
> element in the mapped result.

> The derived collection is **read-only**. `add`, `remove`, `addAll`, `removeAll`,
> `removeIf`, `retainAll`, `clear` — and the list-specific `add(int, E)`,
> `remove(int)`, `sort` — all throw `UnsupportedOperationException`.

## Advantages
- 👁 **Two-layer reactivity**: outer collection + inner observable properties
- 🔒 **No observer leaks**: subscriptions are released when elements leave
- 🤝 **Sharing-friendly**: elements sharing an inner observable share the subscription
- ⛓ **Composable**: combines naturally with `filter` and `mapEach`
