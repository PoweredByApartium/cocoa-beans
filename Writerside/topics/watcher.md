# 🥸 Watcher

<sup>
Available Since 0.0.39
</sup>

```java
public void example() {
    MutableObservable<Integer> observable = Observable.mutable(1);
    
    WatcherManager watcherManager = new WatcherManager();
    AttachedWatcher<Integer> watcher = observable.watch(watcherManager, System.out::println);
    
    watcherManager.heartbeat(); // 1
    watcherManager.heartbeat(); // No new data
    watcherManager.heartbeat(); // No new data

    observable.set(8);
    watcherManager.heartbeat(); // 8
    watcherManager.heartbeat(); // No new data

    observable.set(4);
    observable.set(67);
    watcherManager.heartbeat(); // 67

    observable.set(8);
    observable.set(67);
    watcherManager.heartbeat(); // No new data
    
    watcher.detach(); // Detach watcher from manager
}
```

Start typing here...