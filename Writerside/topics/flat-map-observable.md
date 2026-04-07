# 🖼️ Flat Map Observable
`Observable#flatMap` is a reactive operator that allows mapping an observable value into **another observable** and automatically observing the latest inner observable.

This operator is useful when the value contained inside an observable exposes additional observables that should be followed dynamically.

---

## Introduction

Sometimes an `Observable<T>` contains values that themselves expose observables.
For example, a `PlayerRank` object may expose observable properties like prefix and suffix.

```java
public interface PlayerRank {
    Observable<Component> prefix();
    Observable<Component> suffix();
}
```

Then if we have:
```java
Observable<PlayerRank> rank;
```

We may want to observe the prefix of the **current rank**.

Without `flatMap`, we would need to manually:
 - observe the outer observable
 - unsubscribe from the previous inner observable
 - subscribe to the new inner observable
 - propagate updates
 - ensure observers are not leaked

This pattern quickly becomes repetitive and error-prone.
`flatMap` solves this by automatically switching to the observable returned by the mapper.

## Usage
The `flatMap` operator maps each value to an observable and **observes the latest one**.
<tip>If the source value of the observable is null, the mapper will not be called and return null as well.</tip>

```java
Observable<PlayerRank> rank = player.rankObservable();

Observable<Component> prefix = rank.flatMap(PlayerRank::prefix);
Observable<Component> suffix = rank.flatMap(PlayerRank::suffix);
```

Now the resulting observables update automatically when:
 - the player's rank changes
 - the prefix or suffix of the current rank changes

### Example behaviour:

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="flatMap"/>

## Behavior
`flatMap` dynamically switches to the observable returned by the mapper.
1. The mapper is applied to the current value of the source observable.
2. The returned observable becomes the **active inner observable**.
3. When the outer observable updates:
   - the previous inner observable is unsubscribed
   - the mapper is applied to the new value
   - the new inner observable is subscribed
4. Updates from the active inner observable propagate to observers.

The returned observable always reflects the value of the **latest observable produced by the mapper**.

## Advantages
- 🧠 Eliminates repetitive observer wiring
- 🔒 Prevents observer leaks when switching inner observables
- 🧩 Enables composition of observable structures
- ⚡ Integrates naturally with other observable operators like `Observable#map`