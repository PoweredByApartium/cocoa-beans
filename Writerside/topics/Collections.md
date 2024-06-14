# 📚 Collections

<tip>
Cocoa beans provides a small addition to the Java collections framework.
</tip>

## Dummies
Dummy collections are collection implementations which silently discard any write operations done on them. 
They are useful for disabling or altering existing mechanics within 3rd party code, like NMS. 

```java
import net.apartium.cocoabeans.collect.Dummies;

Map<?, ?> dummyMap = Dummies.dummyMap();
Set<?> dummySet = Dummies.dummySet();

```

## Immutable byte array list
Represents an ordered, immutable, efficient list for storing bytes. Can be useful for storing a wide variety of data, ranging from IP addresses to java class definitions.

```java
import net.apartium.cocoabeans.collect.ImmutableByteArrayList;
// returns an empty instance of the list, identity is guaranteed to be the same across calls
ImmutableByteArrayList.empty();

// transform an existing array into an immutable list
ImmutableByteArrayList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// transform a byte collection into an immutable byte array list
List<Byte> list = new ArrayList<>();
ImmutableByteArrayList.makeBetter(list);
```

## Weight Set
A set implementation that allows for weighted elements to be picked at random, according to their weight.

```java
WeightSet<String> set = new WeightSet<>();
set.add("apple", 1);
set.add("orange", 2);
// the chance of getting an orange (2/3) is twice as high as getting an apple (1/3)
set.pickOne();
```