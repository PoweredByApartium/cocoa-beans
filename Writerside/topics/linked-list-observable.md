# 🔗 LinkedList Observable

<sup>
Available Since 0.0.50
</sup>

## Introduction

A **LinkedListObservable** is a `CollectionObservable` backed by a `java.util.LinkedList`.
It combines the ordered, index-based operations of `AbstractListObservable` with the
head/tail queue operations of `QueueObservable`, making it the right choice when you
need both positional access and efficient FIFO/LIFO processing on an observable collection.

## Creating a LinkedListObservable

```java
// empty linked list
LinkedListObservable<String> names = Observable.linkedList();

// wrap an existing LinkedList (must be modifiable)
LinkedList<Integer> existing = new LinkedList<>(List.of(1, 2, 3));
LinkedListObservable<Integer> scores = Observable.linkedList(existing);
```

## Basic Usage

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="linkedListExample"/>

## Sorting and Index-based Access

Because `LinkedListObservable` implements `AbstractListObservable`, you can insert or
remove elements at a specific position and sort the list:

<code-block lang="java" src="state/CodeSnippets.java" include-symbol="linkedListSortAndIndex"/>

## Queue Operations

`LinkedListObservable` also implements `QueueObservable`, giving you the full
`java.util.Queue` API. See [Queue Observable](queue-observable.md) for details on each
method.

| Method | Throws on empty? | Notifies observers? |
|--------|-----------------|---------------------|
| `offer(E)` | No | Yes (on success) |
| `add(E)` | No | Yes (on success) |
| `poll()` | No — returns `null` | Yes (when non-empty) |
| `remove()` | Yes — `NoSuchElementException` | Yes (when non-empty) |
| `peek()` | No — returns `null` | **No** |
| `element()` | Yes — `NoSuchElementException` | **No** |

> Use `poll()` when you want a safe, null-returning removal. Use `remove()` when you
> expect the queue to be non-empty and want an explicit failure if it is not.

## Observing Changes

All mutating operations (`add`, `remove`, `offer`, `poll`, `sort`, `clear`, …) flag
downstream observables as dirty, which are recomputed lazily on the next `get()` call.
Read-only operations (`peek`, `element`, `get`) never notify observers.

```java
LinkedListObservable<Integer> queue = Observable.linkedList();
Observable<Integer> size = queue.size();

queue.offer(10);
queue.offer(20);
System.out.println(size.get()); // 2

queue.poll();
System.out.println(size.get()); // 1
```

## Sort Optimisation

`sort()` compares the current order against the target order before sorting.
If the list is already sorted by the given comparator, **no observers are notified**.

```java
LinkedListObservable<Integer> list = Observable.linkedList();
list.addAll(List.of(1, 2, 3));

list.sort(Integer::compareTo); // already sorted – observers are NOT notified
```

Calling `sort(null)` always throws `NullPointerException`.
