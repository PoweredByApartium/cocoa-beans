# 🧰 Collection Observable

<sup>
Available Since 0.0.39
</sup>

## Introduction
A Collection Observable is an observable that wraps around a collection (like a list or set) and tracks changes to its contents, 
not just its reference

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
