# 🔬 Compound Observable

<sup>
Available Since 0.0.39
</sup>

```java
public void example() {
    MutableObservable<String> name = Observable.mutable("Kfir");
    MutableObservable<Integer> num = Observable.mutable(123);

    ObservableCompound<CompoundRecords.RecordOf2<String, Integer>>
            compound = Observable.compound(name, num);
    
    print(compound.get().arg0()); // Output "Kfir"
    print(compound.get().arg1()); // Output 123
}
```

Start typing here...