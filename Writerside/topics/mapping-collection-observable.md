# 🪞 Mapping Collection Observable

<sup>
Available Since 0.0.50
</sup>

## Introduction
`mapEach` is a **per-element** transformation operator on a `CollectionObservable`.
It produces a derived collection whose elements come from applying a mapper to **each element** of the source.

This is different from `Observable#map`, which transforms the *whole* collection
into a single value. `mapEach` keeps the **collection-shape** and transforms only
its elements:

```java
ListObservable<GamePlayer> players = Observable.list();

ListObservable<String> names = players.mapEach(GamePlayer::name);
```

If your mapper returns an `Observable<R>` you want to follow live, use [](flat-mapping-collection-observable.md) instead.

## Usage
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="mapEachSample"/>

## Behavior
- Type-preserving:
  - `ListObservable<E>` → `ListObservable<R>`
  - `SetObservable<E>` → `SetObservable<R>`
- The mapper runs **lazily** on `get()`, only when the source has changed since the previous call.
- The result is **cached** between calls; the mapper is **not** re-run for unrelated reads.
- Updates fire when the source collection changes (add / remove / clear / reorder).
  - `mapEach` does **not** subscribe to anything inside the mapped values — only the source collection.

## Notes per collection type
| Source                  | Result                  | Effect of duplicates                                  |
|-------------------------|-------------------------|-------------------------------------------------------|
| `ListObservable<E>`     | `ListObservable<R>`     | Order and duplicates preserved                        |
| `SetObservable<E>`      | `SetObservable<R>`      | Mapped collisions deduplicate (set semantics)         |

> The derived collection is **read-only**. `add`, `remove`, `addAll`, `removeAll`,
> `removeIf`, `retainAll`, `clear` — and the list-specific `add(int, E)`,
> `remove(int)`, `sort` — all throw `UnsupportedOperationException`.

## Advantages
- 🪶 **Cheap**: lazy + cached, mapper runs once per element per source change
- 🧱 **Composable**: chains naturally with `filter` and `flatMapEach`
- 🧹 **No leaks**: nothing else is observed; only the source collection
- ⛓ **Type-preserving**: a mapped `ListObservable` stays a `ListObservable`
