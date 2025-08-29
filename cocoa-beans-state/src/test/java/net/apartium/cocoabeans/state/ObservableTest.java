package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ObservableTest {

    @Test
    void removeObserverImmutable() {
        assertFalse(Observable.immutable(1).removeObserver(null));
        assertFalse(Observable.immutable(1).removeObserver(n -> {}));
    }

    @Test
    void removeObserverMutable() {
        MutableObservable<Integer> num = Observable.mutable(12);

        Observer observer = n -> {};
        num.observe(observer);
        num.set(100);
        assertTrue(num.removeObserver(observer));
    }

    @Test
    void removeObserverCompound() {
        MutableObservable<Integer> num = Observable.mutable(12);
        var compound = Observable.compound(num);

        Observer observer = n -> {};
        compound.observe(observer);
        num.set(100);
        assertTrue(compound.removeObserver(observer));
    }

    @Test
    void removeObserverMapped() {
        MutableObservable<Integer> num = Observable.mutable(12);
        Observable<Boolean> isEven = num.map(n -> n % 2 == 0);

        Observer observer = n -> {};
        isEven.observe(observer);
        num.set(100);
        assertTrue(isEven.removeObserver(observer));
    }

    @Test
    void testValueState() {
        MutableObservable<Integer> num = Observable.mutable(9);
        assertEquals(9, ((int) num.get()));
    }

    @Test
    void setValueStateTest() {
        MutableObservable<Integer> num = Observable.mutable(9);
        num.set(20);
        assertEquals(20, ((int) num.get()));
    }


    @Test
    void empty() {
        Observable<Integer> empty = Observable.empty();
        assertEquals(Observable.empty(), empty);
        assertNull(empty.get());

    }

    @Test
    void listAddAndAddAllAndRetainAllAndRemoveFailed() {
        ListObservable<String> list = Observable.list(new List<>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }

            @Override
            public boolean add(String integer) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends String> collection) {
                return false;
            }

            @Override
            public boolean addAll(int i, Collection<? extends String> collection) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {
                /*
                 * fakes list with custom size
                 */
            }

            @Override
            public String get(int i) {
                return null;
            }

            @Override
            public String set(int i, String s) {
                return null;
            }

            @Override
            public void add(int i, String s) {
                /*
                 * fakes list that doesn't add and just ignore it
                 */
            }

            @Override
            public String remove(int i) {
                return null;
            }

            @Override
            public int indexOf(Object o) {
                return 0;
            }

            @Override
            public int lastIndexOf(Object o) {
                return 0;
            }

            @Override
            public ListIterator<String> listIterator() {
                return null;
            }

            @Override
            public ListIterator<String> listIterator(int i) {
                return null;
            }

            @Override
            public List<String> subList(int i, int i1) {
                return List.of();
            }
        });

        assertFalse(list.add("test"));
        assertFalse(list.addAll(List.of("test", "wow")));
        assertFalse(list.retainAll(List.of("lol", "wow")));
        assertFalse(list.removeAll(List.of("lol", "wow")));

        list.add(0, "lol");
        assertNull(list.remove(0));
    }

    @Test
    void mappedObservableFlagDirtyNotBase() {
        Observable<Integer> num = Observable.mutable(9);

        MappedObservable<Integer, Boolean> map = (MappedObservable<Integer, Boolean>) num.map(n -> n % 2 == 0);

        try {
            map.flagAsDirty(null);
            Field isDirty = map.getClass().getDeclaredField("isDirty");
            isDirty.setAccessible(true);

            assertFalse(isDirty.getBoolean(map));

            map.flagAsDirty(map);
            assertFalse(isDirty.getBoolean(map));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }




    }

    @Test
    void hadChangedCollection() {
        MutableObservable<String> playerName = Observable.mutable("test");
        ListObservable<Integer> list = Observable.list();

        AtomicInteger count = new AtomicInteger();
        Observable<String> map = Observable.compound(playerName, list).map(args -> {
            count.incrementAndGet();

            return args.arg0() + ": " + Arrays.toString(args.arg1().toArray(new Integer[0]));
        });

        assertEquals(0, count.get());
        assertEquals("test: []", map.get());
        assertEquals(1, count.get());

        list.add(90);
        assertEquals(1, count.get());

        list.add(74);
        assertEquals(1, count.get());

        assertEquals("test: [90, 74]", map.get());
        assertEquals(2, count.get());

    }

    @Test
    void mapListObservable() {
        ListObservable<String> names = Observable.list();
        AtomicInteger count = new AtomicInteger();

        Observable<List<String>> namesLength = names.map(list -> {
            count.incrementAndGet();
            return list.stream()
                            .map(name -> name + ": " + name.length())
                            .toList();
        });

        assertEquals(0, count.get());

        assertEquals(List.of(), namesLength.get());
        assertEquals(1, count.get());

        names.add("Kfir");
        assertEquals(1, count.get());

        assertEquals(List.of("Kfir: 4"), namesLength.get());
        assertEquals(2, count.get());

        names.add("Elion");
        assertEquals(2, count.get());
        names.add("Lior");
        assertEquals(2, count.get());

        assertEquals(List.of("Kfir: 4", "Elion: 5", "Lior: 4"), namesLength.get());
        assertEquals(3, count.get());

    }

    @Test
    void testCompoundState() {
        MutableObservable<Integer> num = Observable.mutable(9);

        AtomicInteger isEvenCalled = new AtomicInteger();

        Observable<Boolean> isEven = Observable.compound(
                list -> {
                    isEvenCalled.addAndGet(1);
                    return ((int) list.get(0)) % 2 == 0;
                },
                List.of(num)
        );

        AtomicInteger parityCalled = new AtomicInteger();

        Observable<String> parity = Observable.compound(
                list -> {
                    parityCalled.addAndGet(1);
                    return ((boolean) list.get(0)) ? "even" : "odd";
                },
                List.of(isEven)
        );

        assertEquals("odd", parity.get());
        assertEquals(1, isEvenCalled.get());
        assertEquals(1, parityCalled.get());

        num.set(20);

        assertEquals("even", parity.get());
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        num.set(24);

        assertEquals("even", parity.get());
        assertEquals(3, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        num.set(24);

        assertEquals("even", parity.get());
        assertEquals(3, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        num.set(7);
        num.set(7);

        assertFalse(isEven.get());
        assertFalse(isEven.get());

        assertEquals("odd", parity.get());
        assertEquals("odd", parity.get());
        assertEquals(4, isEvenCalled.get());
        assertEquals(3, parityCalled.get());

        num.set(9);

        assertEquals("odd", parity.get());
        assertEquals(5, isEvenCalled.get());
        assertEquals(3, parityCalled.get());
    }

    @Test
    void tryingToModifyCompoundState() {
        MutableObservable<Integer> num = Observable.mutable(9);
        Observable<Boolean> isEven = Observable.compound(
                list -> {
                    assertThrows(UnsupportedOperationException.class, () -> list.remove(0));
                    return ((int) list.get(0)) % 2 == 0;
                },
                List.of(num)
        );

        assertFalse(isEven.get());

    }

    @Test
    void testCompoundState2() {

        MutableObservable<Integer> num0 = Observable.mutable(7);
        MutableObservable<Integer> num1 = Observable.mutable(14);

        AtomicInteger addToNumberCalled = new AtomicInteger();
        Observable<Integer> addToNumber = Observable.compound(num0, num1).map(
                nums -> {
                    addToNumberCalled.addAndGet(1);
                    return nums.arg0() + nums.arg1();
                }
        );

        assertEquals(21, addToNumber.get());
        assertEquals(1, addToNumberCalled.get());

        num0.set(4);


        assertEquals(18, addToNumber.get());
        assertEquals(2, addToNumberCalled.get());

        num0.set(4);

        assertEquals(18, addToNumber.get());
        assertEquals(2, addToNumberCalled.get());

        num0.set(9);
        num1.set(15);

        assertEquals(24, addToNumber.get());
        assertEquals(3, addToNumberCalled.get());

        AtomicInteger isEvenCalled = new AtomicInteger();
        Observable<Boolean> isEven = Observable.compound(addToNumber).map(num -> {
                    isEvenCalled.addAndGet(1);
                    return num.arg0() % 2 == 0;
        });

        AtomicInteger parityCalled = new AtomicInteger();
        Observable<String> parity = Observable.compound(isEven).map(
                even -> {
                    parityCalled.addAndGet(1);
                    return even.arg0() ? "even" : "odd";
                });

        AtomicInteger textCalled = new AtomicInteger();
        Observable<String> text = Observable.compound(addToNumber, parity).map(args -> {
                    textCalled.addAndGet(1);
                    return args.arg0() + " is " + args.arg1();
        });



        assertEquals("24 is even", text.get());
        assertEquals(1, isEvenCalled.get());
        assertEquals(1, parityCalled.get());
        assertEquals(1, textCalled.get());
        assertEquals(3, addToNumberCalled.get());

        num0.set(11);

        assertEquals("26 is even", text.get());
        assertEquals(2, isEvenCalled.get());
        assertEquals(1, parityCalled.get());
        assertEquals(2, textCalled.get());
        assertEquals(4, addToNumberCalled.get());

    }

    @Test
    void nullStateValue() {
        MutableObservable<String> name = Observable.mutable(null);

        AtomicInteger helloWorldCalled = new AtomicInteger();
        Observable<String> helloWorld = Observable.compound(
                list -> {
                    helloWorldCalled.addAndGet(1);
                    return "Hello, World " + list.get(0);
                },
                List.of(name)
        );

        assertEquals("Hello, World null", helloWorld.get());
        assertEquals(1, helloWorldCalled.get());

        name.set("Bob");

        assertEquals("Hello, World Bob", helloWorld.get());
        assertEquals(2, helloWorldCalled.get());
    }

    @Test
    void linkedHashMapSaveOrder() {
        for (int j = 0; j < 1000; j++) {
            LinkedHashMap<Integer, Boolean> isEven = new LinkedHashMap<>();

            for (int i = 1; i <= 1000; i++) {
                isEven.put(i, i % 2 == 0);
            }

            int index = 1;
            for (Map.Entry<Integer, Boolean> entry : isEven.entrySet()) {
                assertEquals(index, entry.getKey());
                assertEquals(index % 2 == 0, entry.getValue());
                index++;
            }
        }
    }

    @Test
    void edgeCaseTest() {

        MutableObservable<Integer> num = Observable.mutable(9);

        AtomicInteger isEvenCalled = new AtomicInteger();

        Observable<Boolean> isEven = Observable.compound(
                list -> {
                    isEvenCalled.addAndGet(1);
                    return ((int) list.get(0)) % 2 == 0;
                },
                List.of(num)
        );

        AtomicInteger parityCalled = new AtomicInteger();

        Observable<String> parity = Observable.compound(
                list -> {
                    parityCalled.addAndGet(1);
                    return ((boolean) list.get(0)) ? "even" : "odd";
                },
                List.of(isEven)
        );

        AtomicInteger textCalled = new AtomicInteger();

        Observable<String> text = Observable.compound(
                list -> {
                    textCalled.addAndGet(1);
                    return ((boolean) list.get(0)) ? "this is even" : "this is odd";
                },
                List.of(isEven)
        );

        assertEquals("odd", parity.get());
        assertEquals(1, isEvenCalled.get());
        assertEquals(1, parityCalled.get());
        assertEquals("this is odd", text.get());
        assertEquals(1, isEvenCalled.get());
        assertEquals(1, textCalled.get());

        num.set(10);
        assertEquals("even", parity.get());
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        num.set(10);
        assertEquals("even", parity.get());
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        assertEquals("this is even", text.get());
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, textCalled.get());

        num.set(11);
        assertEquals("this is odd", text.get());

        assertEquals(3, isEvenCalled.get());
        assertEquals(3, textCalled.get());
        num.set(15);

        assertEquals("this is odd", text.get());

        assertEquals(4, isEvenCalled.get());
        assertEquals(3, textCalled.get());

        assertEquals("odd", parity.get());
        assertEquals(4, isEvenCalled.get());
        assertEquals(3, textCalled.get());
    }

    @Test
    void testListState() {
        ListObservable<String> names = Observable.list();

        AtomicInteger stateCalled = new AtomicInteger();

        Observable<String> state = names.map(namesList -> {
                    stateCalled.addAndGet(1);
                    return "[" + String.join(", ", namesList) + "]";
                }
        );

        names.add("test");

        assertEquals("[test]", state.get());
        assertEquals(1, stateCalled.get());

        assertEquals("[test]", state.get());
        assertEquals(1, stateCalled.get());

        names.add("test2");

        assertEquals("[test, test2]", state.get());
        assertEquals(2, stateCalled.get());

        assertEquals("[test, test2]", state.get());
        assertEquals(2, stateCalled.get());

        names.add("test3");

        assertEquals("[test, test2, test3]", state.get());
        assertEquals(3, stateCalled.get());

        assertEquals("[test, test2, test3]", state.get());
        assertEquals(3, stateCalled.get());

        names.remove("test3");
        names.add("test3");


        assertEquals("[test, test2, test3]", state.get());
        assertEquals(3, stateCalled.get());

        assertEquals("[test, test2, test3]", state.get());
        assertEquals(3, stateCalled.get());

        names.removeIf(s -> s.equals("test3"));

        assertEquals("[test, test2]", state.get());
        assertEquals(4, stateCalled.get());

        names.remove("test3");

        assertEquals("[test, test2]", state.get());
        assertEquals(4, stateCalled.get());

        names.removeIf(s -> s.equals("test3"));

        assertEquals("[test, test2]", state.get());
        assertEquals(4, stateCalled.get());

        names.add("test3");
        names.remove("test3");

        assertEquals("[test, test2]", state.get());
        assertEquals(4, stateCalled.get());

        names.add("test3");
        names.remove(2);

        assertEquals("[test, test2]", state.get());
        assertEquals(4, stateCalled.get());

        assertThrows(IndexOutOfBoundsException.class ,() -> names.remove(2));

        names.addAll(List.of("test3", "test3_1", "test3_4"));

        assertEquals("[test, test2, test3, test3_1, test3_4]", state.get());
        assertEquals(5, stateCalled.get());

        names.add(0, "ah???");

        assertEquals("[ah???, test, test2, test3, test3_1, test3_4]", state.get());
        assertEquals(6, stateCalled.get());

        names.add(3, "cool_name");

        assertEquals("[ah???, test, test2, cool_name, test3, test3_1, test3_4]", state.get());
        assertEquals(7, stateCalled.get());

        names.sort(String::compareTo);

        assertEquals("[ah???, cool_name, test, test2, test3, test3_1, test3_4]", state.get());
        assertEquals(8, stateCalled.get());

        names.sort(String::compareTo);

        assertEquals("[ah???, cool_name, test, test2, test3, test3_1, test3_4]", state.get());
        assertEquals(8, stateCalled.get());

        names.clear();

        assertEquals("[]", state.get());
        assertEquals(0, names.get().size());
        assertEquals(9, stateCalled.get());

        names.clear();
        assertEquals("[]", state.get());
        assertEquals(9, stateCalled.get());

        names.addAll(List.of("test0", "test1", "test2", "test0"));
        assertTrue(names.retainAll(List.of("test0", "test2")));

        assertEquals("[test0, test2, test0]", state.get());
        assertEquals(10, stateCalled.get());

        assertThrowsExactly(IndexOutOfBoundsException.class, () -> names.remove(4));
        assertThrowsExactly(NullPointerException.class, () -> names.sort(null));

        assertEquals("[test0, test2, test0]", state.get());
        assertEquals(10, stateCalled.get());

        names.addAll(List.of("test1", "test3", "test4", "test1"));
        assertEquals(10, stateCalled.get());

        assertEquals("[test0, test2, test0, test1, test3, test4, test1]", state.get());
        assertEquals(11, stateCalled.get());

        names.remove("test0");
        assertEquals(11, stateCalled.get());


        assertEquals("[test2, test0, test1, test3, test4, test1]", state.get());
        assertEquals(12, stateCalled.get());

        names.removeAll(List.of("test1", "test2"));
        assertEquals(12, stateCalled.get());


        assertEquals("[test0, test3, test4]", state.get());
        assertEquals(13, stateCalled.get());

    }

    @Test
    void coolCompoundTest() {
        MutableObservable<Integer> number = Observable.mutable(9);

        AtomicInteger isEvenCalled = new AtomicInteger();

        Observable<Boolean> isEven = Observable.compound(number).map(num -> {
            isEvenCalled.addAndGet(1);
            return num.arg0() % 2 == 0;
        });

        assertFalse(isEven.get());
        assertEquals(1, isEvenCalled.get());

        number.set(10);

        assertTrue(isEven.get());
        assertEquals(2, isEvenCalled.get());

        number.set(10);

        assertTrue(isEven.get());
        assertEquals(2, isEvenCalled.get());

        AtomicInteger parityCalled = new AtomicInteger();
        Observable<String> parity = Observable.compound(isEven).map(even -> {
            parityCalled.addAndGet(1);
            return even.arg0() ? "Even" : "Odd";
        });

        assertEquals("Even", parity.get());
        assertEquals(1, parityCalled.get());
        assertEquals(2, isEvenCalled.get());

        number.set(11);
        assertEquals("Odd", parity.get());
        assertEquals(2, parityCalled.get());
        assertEquals(3, isEvenCalled.get());

        AtomicInteger textCalled = new AtomicInteger();
        Observable<String> text = Observable.compound(number, parity).map(values -> {
            textCalled.addAndGet(1);
            return values.arg0() + " is " + values.arg1();
        });

        assertEquals("11 is Odd", text.get());
        assertEquals(1, textCalled.get());
        assertEquals(2, parityCalled.get());
        assertEquals(3, isEvenCalled.get());

        number.set(9);
        assertEquals("9 is Odd", text.get());
        assertEquals(2, textCalled.get());
        assertEquals(2, parityCalled.get());
        assertEquals(4, isEvenCalled.get());

        number.set(42);
        assertEquals("42 is Even", text.get());
        assertEquals(3, textCalled.get());
        assertEquals(3, parityCalled.get());
        assertEquals(5, isEvenCalled.get());

        ListObservable<String> players = Observable.list();
        players.add("ikfir");
        players.add("voigon");

        Observable<List<String>> loreState = players.map(list -> {
            List<String> lore = new ArrayList<>();
            for (String player : list)
                lore.add(" - " + player);
            return lore;
        });

        assertEquals(List.of(" - ikfir", " - voigon"), loreState.get() );

    }

    @Test
    void watcherTest() {
        MutableObservable<Integer> number = Observable.mutable(9);
        WatcherManager watcherManager = new WatcherManager();


        AtomicInteger current = new AtomicInteger(9);

        number.lazyWatch(watcherManager, num -> {
            assertEquals(current.get(), num);
        });

        watcherManager.heartbeat();
        watcherManager.heartbeat();
        watcherManager.heartbeat();

        number.set(73);
        current.set(73);

        watcherManager.heartbeat();
        watcherManager.heartbeat();

        number.set(9);
        current.set(9);
        watcherManager.heartbeat();
        number.set(9);
        number.set(11);
        number.set(9);

        watcherManager.heartbeat();
        watcherManager.heartbeat();
    }

}
