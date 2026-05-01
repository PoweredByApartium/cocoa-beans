# 🚥 Queue Observable

<sup>
Available Since 0.0.50
</sup>

**QueueObservable** is an interface that extends `CollectionObservable` and adds the
standard `java.util.Queue` operations to any observable collection whose backing store
is a `Queue`. It lets you work with a queue in the reactive style: every mutation
automatically flags downstream observables as dirty.

## Interface Definition

```java
public interface QueueObservable<E, C extends Queue<E>> extends CollectionObservable<E, C> {
    boolean add(E element);
    boolean offer(E element);
    E remove();
    E poll();
    E element();
    E peek();
}
```

## Implementations

| Class | Backing collection |
|-------|--------------------|
| `LinkedListObservable` | `java.util.LinkedList` |

See [LinkedList Observable](linked-list-observable.md) for the concrete implementation.

## Method Reference

### Inserting elements

| Method | Description | Notifies observers |
|--------|-------------|-------------------|
| `add(E)` | Adds element; returns `true` on success | Yes |
| `offer(E)` | Offers element; returns `true` on success, `false` when capacity is exceeded | Yes (on success) |

Both methods append to the **tail** of the queue for a `LinkedList`-backed
implementation.

### Retrieving and removing the head

| Method | Queue empty → | Notifies observers |
|--------|--------------|-------------------|
| `poll()` | Returns `null` | Yes (when non-empty) |
| `remove()` | Throws `NoSuchElementException` | Yes (when non-empty) |

Use `poll()` for a null-safe removal and `remove()` when you expect the queue to be
non-empty and want a hard failure if it is not.

### Inspecting the head (non-mutating)

| Method | Queue empty → | Notifies observers |
|--------|--------------|-------------------|
| `peek()` | Returns `null` | **Never** |
| `element()` | Throws `NoSuchElementException` | **Never** |

`peek()` and `element()` are purely read-only — calling them will **never** flag
observers as dirty.

## FIFO Queue Pattern

`QueueObservable` is ideal for modelling first-in-first-out pipelines whose size
(or head element) other parts of your application need to observe reactively:

```java
LinkedListObservable<String> taskQueue = Observable.linkedList();
Observable<Integer> queueDepth = taskQueue.size();

// producer
taskQueue.offer("task-1");
taskQueue.offer("task-2");
taskQueue.offer("task-3");

System.out.println(queueDepth.get()); // 3

// consumer
while (taskQueue.peek() != null) {
    String task = taskQueue.poll();
    System.out.println("Processing: " + task);
}

System.out.println(queueDepth.get()); // 0
```

## Observer Notification Rules

A quick summary of which operations trigger observer notifications:

| Operation | Triggers notification |
|-----------|-----------------------|
| `add` / `offer` (success) | Yes |
| `poll` / `remove` (non-empty) | Yes |
| `poll` / `remove` (empty) | No |
| `peek` / `element` | **No** |
| `clear` (non-empty) | Yes |
| `clear` (already empty) | No |
| `sort` (order changes) | Yes |
| `sort` (already sorted) | No |
