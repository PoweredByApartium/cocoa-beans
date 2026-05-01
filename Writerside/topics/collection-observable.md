# 🧰 Collection Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction
A ``CollectionObservable`` is an observable that wraps around a collection (like a list or set) and tracks changes to its contents.
It differs from [](compound-observable.md) by allowing the length of the collection to change dynamically, at the expense of having a single element type.

## 🧠 Why is it special?
Most observables only react when their **value changes**—for example,
when you assign a new list.
But what if you just add or remove an item from the existing list?
That's where `CollectionObservable` shines:
- It knows when **items are added, removed, or reordered.**
- Even if the reference stays the same, it can still detect **internal changes.**

## 📦 What does it do?
- Lets you observe **dynamic collections**.
- Automatically updates anything watching it—like UI elements, counters, or filtered views—when the collection changes.
- Works seamlessly with tools like `MappedObservable` or filters/sorts.

## 🌊 Use Case Examples
- A list of players in a game lobby that updates when players join or leave.
- A cart in a shopping app that reacts when items are added or removed.
- A filtered view that always shows only the matching items.

## Implementations
- [](list-observable.md)
- [](set-observable.md)
- [](filtering-collection-observable.md)
- [](queue-observable.md)
  - [](linked-list-observable.md)