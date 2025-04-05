# 📋 Mutable Observable

<sup>
Available Since 0.0.39
</sup>

```java
public void example() {
    MutableObservable<Integer> observable = Observable.mutable(123);
    
    print(observable.get()); // 123

    observable.set(456); // flag his observers dirty
    
    print(observable.get()); // 456
}
```
Start typing here...