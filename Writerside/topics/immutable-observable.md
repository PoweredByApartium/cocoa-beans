# 🗿 Immutable Observable

<sup>
Available Since 0.0.39
</sup>

```java
public void example() {
    Observable<Integer> observable = Observable.immutable(123);
    
    print(observable.get()); // Always return 123 and not flag anyone dirty
}
```

Start typing here...