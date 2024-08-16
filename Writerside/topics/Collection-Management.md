# Collection Management

**Table of content:**
- [AbstractManager](#AbstractManager)
- [AbstractCollectionManager](#AbstractCollectionManager)

## AbstractManager

`AbstractManager` is an abstract class that provides common operations for managing a map. It includes basic methods for adding, removing, and retrieving entries by key.

### Usage example

```java
import java.util.Optional;

public class ExampleManager extends AbstractManager<String, Integer> {

    public ExampleManager() {
        super();
    }

    public Optional<Integer> getValue(String key) {
        return Optional.ofNullable(map.get(key));
    }

    public static void main(String[] args) {
        Example manager = new Example();
        manager.put("one", 1);
        manager.put("two", 2);

        Optional<Integer> value = manager.getValue("one");
        value.ifPresent(v -> System.out.println("Value: " + v)); // Output: Value: 1
    }
}
```

## AbstractCollectionManager

`AbstractCollectionManager` is an abstract class designed to manage a collection. It provides common operations for adding, removing, and checking elements, as well as querying elements using a predicate. The get method returns an Optional<Stream<E>>, allowing you to handle cases where no elements match the predicate.

### Usage example

**Set example:**
```java
import java.util.HashSet;
import java.util.Set;

public class SetCollectionManager<E> extends AbstractCollectionManager<Set<E>, E> {

    public SetCollectionManager() {
        super(new HashSet<>());
    }
    
    // Additional set-specific methods can be added here
}

// Usage
public class Example {
    public static void main(String[] args) {
        SetCollectionManager<String> manager = new SetCollectionManager<>();
        manager.add("apple");
        manager.add("banana");

        System.out.println(manager.contains("apple")); // Output: true
        System.out.println(manager.contains("cherry")); // Output: false
    }
}
```

**List example:**
```java
import java.util.ArrayList;
import java.util.List;

public class ListCollectionManager<E> extends AbstractCollectionManager<List<E>, E> {

    public ListCollectionManager() {
        super(new ArrayList<>());
    }
    
    // Additional list-specific methods can be added here
}

// Usage
public class Example {
    public static void main(String[] args) {
        ListCollectionManager<String> manager = new ListCollectionManager<>();
        manager.add("apple");
        manager.add("banana");

        System.out.println(manager.contains("apple")); // Output: true
        System.out.println(manager.contains("cherry")); // Output: false
    }
}
```