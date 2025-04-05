# 🔗 List Observable

<sup>
Available Since 0.0.39
</sup>

```java
public void example() {
    ListObservable<String> names = Observable.list(); // An ArrayList<String>
    
    names.add("Kfir");
    print(names); // ["Kfir"]

    names.add("Lior");
    print(names); // ["Kfir", "Lior"]

    names.addAll("Kfir", "Tom");
    print(names); // ["Kfir", "Lior", "Kfir", "Tom"]

    names.remove("Kfir");
    print(names); // ["Lior", "Kfir", "Tom"]
}
```

Start typing here...