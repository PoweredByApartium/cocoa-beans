# 📺 Observable api

<sup>
Available Since 0.0.39
</sup>

**Table of content:**
- [Introduction](#introduction)
- [Usage](#usage)
- [How it work](#how-it-work)
- [Advantages](#advantages)
- [What next](#what-next)

## Introduction
The `Observable<T>` interface represents a reactive, stateful object that can be watched for changes. It provides a consistent and flexible API for building reactive UIs, data flows, and dynamic logic in applications.

Observables are designed to be either immutable or mutable, with support for compound states that automatically derive values from other observables. It includes built-in support for list-based observables, mapped observables, and reactive recomposition through dependency tracking.

This system is designed to be lightweight, composable, and extensible.

## Usage

```java
public void example() {
    MutableObservable<Integer> num = Observable.mutable(0);
    Observable<Boolean> isEven = num.map(n -> n % 2 == 0);
    Observable<String> parity = Observable.compound(num, isEven)
            .map((args) -> args.arg0() + ": " + args.arg1());

    print(parity.get()); // output "0: false"

    num.set(9); // Flag isEven and parity as dirty but don't recompute yet

    print(parity.get()); // output "9: false"

    num.set(42); // Flag isEven and parity as dirty but don't recompute yet

    print(parity.get()); // output "42: true"

    num.set(21); // Flag isEven and parity as dirty but don't recompute yet
    num.set(42); // Flag isEven and parity as dirty but don't recompute yet

    print(parity.get()); // output "42: true" but didn't need to recompute
}
```


```java
public void example() {
    ListObservable<String> names = Observable.list(); // Create an ArrayList<String>

    print(names.get()); // []

    names.add("Kfir");

    print(names.get()); // ["Kfir"]

    names.addAll(List.of("Lior", "Tom"));

    print(names.get()); // ["Kfir", "Lior", "Tom"]

    names.remove("Tom");

    print(names.get()); // ["Kfir", "Lior"]

    Observable<List<String>> namesLength = names.map((list) ->
            list.stream()
                    .map(name -> name + ": " + name.length())
                    .toList()
    );

    print(namesLength.get()); // ["Kfir: 4", "Lior: 4"]

    names.add("Elion");
    print(namesLength.get()); // ["Kfir: 4", "Lior: 4", "Elion: 5"]
}
```

## How it work


## Advantages
✅ Type-safe reactive model with strong generics and records.

♻️ Immutable and Mutable models support both predictable state and dynamic changes.

🔗 Composable—easy to derive, map, and observe values.

🧩 Extensible—can be combined into complex reactive graphs with minimal boilerplate.


🚀 Designed for performance and clarity with lazy recomputation and controlled observers.

## What next
* [](immutable-observable.md)
* [](mutable-observable.md)
* [](compound-observable.md)
* [](mapped-observable.md)
* [](collection-observable.md):
    * [](list-observable.md)
* [](observer.md)
* [](watcher.md)
* [Javadocs](https://cocoa-beans.apartium.net/%version%/common/net/apartium/cocoabeans/state/package-summary.html)