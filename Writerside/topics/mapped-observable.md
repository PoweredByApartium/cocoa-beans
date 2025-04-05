# 🗺️ Mapped Observable

<sup>
Available Since 0.0.39
</sup>

```java
public void example() {
    MutableObservable<Integer> number = Observable.mutable(123);
    Observable<Boolean> isEven = number.map((num) -> num % 2 == 0);
    
    print(isEven.get()); // Output "false" because 123 is odd

    number.set(8);
    print(isEven.get()); // Output "true" because 8 is even

    number.set(11);
    number.set(8);
    print(isEven.get()); // Output "true" because 8 is even and won't recompute because same value
    
    Observable<String> isEvenParity = isEven.map(
            (b) -> b 
                    ? "even"
                    : "odd"
    );
    
    print(isEvenParity.get()); // Output "even"

    number.set(16);
    
    print(isEvenParity.get()); // Output "even" and isEvenParity won't be recompute because isEven at come up with same value
}
```

Start typing here...