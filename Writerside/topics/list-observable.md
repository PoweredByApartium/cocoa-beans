# 🔗 List Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction
A **ListObservable** is a specialized `CollectionObservable` that provides ``java.util.List`` specific operations. 

## Usage
<code-block lang="java" src="state/CodeSnippets.java" include-symbol="simpleList"/>

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="listOrderAndMapped"/>

## Replacing an Element

<sup>
Available Since 0.0.53
</sup>

`set(int index, E element)` replaces the element stored at `index` and returns the element
that was there before — the same contract as `java.util.List#set(int, Object)`.

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="listSet"/>

### Behavior

| Situation | Result |
|-----------|--------|
| The stored element changes | The list is updated and observers are flagged dirty |
| The new element `equals` the stored one | No-op — the list is untouched and **no observer is notified** |
| `index` is out of range | `IndexOutOfBoundsException`, nothing is modified or notified |

- `set` **never changes the size** of the list, so `size()` does not change value.
- Only the element at `index` is affected — duplicates elsewhere in the list are left alone.
- Derived, read-only views (`filter`, `mapEach`, `flatMapEach`, `sorted`, `as(...)`) throw
  `UnsupportedOperationException`, exactly like `add(int, E)`, `remove(int)` and `sort`.
  Call `set` on the source list and let the change propagate down the chain.
