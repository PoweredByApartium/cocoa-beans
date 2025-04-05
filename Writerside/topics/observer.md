# 🔍 Observer

<sup>
Available Since 0.0.39
</sup>

```java
public class MyObserver<T> implements Observer {

    private final Observable<T> target;
    private boolean isDirty = false;
    
    private MyObserver(Observable<T> target) {
        this.target = target;
    }
    
    @Override
    public void flagAsDirty(Observable<?> observable) {
        if (observable != target)
            return;
        
        isDirty = true;
       // Using heartbeat or other way to get
       // the value after it has been flagged as dirty
       // this is simple example no changes check 
    }

} 
```

Start typing here...