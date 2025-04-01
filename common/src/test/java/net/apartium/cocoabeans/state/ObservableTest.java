package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ObservableTest {

    @Test
    public void testValueState() {
        MutableObservable<Integer> num = Observable.mutable(9);
        assertEquals(9, ((int) num.get()));
    }

    @Test
    public void setValueStateTest() {
        MutableObservable<Integer> num = Observable.mutable(9);
        num.set(20);
        assertEquals(20, ((int) num.get()));
    }

    @Test
    public void testCompoundState() {
        MutableObservable<Integer> num = Observable.mutable(9);

        AtomicInteger isEvenCalled = new AtomicInteger();

        Observable<Boolean> isEven = Observable.compound(
                (list) -> {
                    isEvenCalled.addAndGet(1);
                    return ((int) list.get(0)) % 2 == 0;
                },
                List.of(num)
        );

        AtomicInteger parityCalled = new AtomicInteger();

        Observable<String> parity = Observable.compound(
                (list) -> {
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
    public void tryingToModifyCompoundState() {
        MutableObservable<Integer> num = Observable.mutable(9);
        Observable<Boolean> isEven = Observable.compound(
                (list) -> {
                    assertThrows(UnsupportedOperationException.class, () -> list.remove(0));
                    return ((int) list.get(0)) % 2 == 0;
                },
                List.of(num)
        );

        assertFalse(isEven.get());

    }

    @Test
    public void testCompoundState2() {

        MutableObservable<Integer> num0 = Observable.mutable(7);
        MutableObservable<Integer> num1 = Observable.mutable(14);

        AtomicInteger addToNumberCalled = new AtomicInteger();
        Observable<Integer> addToNumber = Observable.compound(num0, num1).map(
                (nums) -> {
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
        Observable<Boolean> isEven = Observable.compound(addToNumber).map((num) -> {
                    isEvenCalled.addAndGet(1);
                    return num.arg0() % 2 == 0;
        });

        AtomicInteger parityCalled = new AtomicInteger();
        Observable<String> parity = Observable.compound(isEven).map(
                (even) -> {
                    parityCalled.addAndGet(1);
                    return even.arg0() ? "even" : "odd";
                });

        AtomicInteger textCalled = new AtomicInteger();
        Observable<String> text = Observable.compound(addToNumber, parity).map(args -> {
                    textCalled.addAndGet(1);
                    return args.arg0() + " is " + args.arg1();
        });


        // TODO Fixed order random

        assertEquals(text.get(), "24 is even");
        assertEquals(1, isEvenCalled.get());
        assertEquals(1, parityCalled.get());
        assertEquals(1, textCalled.get());
        assertEquals(3, addToNumberCalled.get());

        num0.set(11);

        assertEquals(text.get(), "26 is even");
        assertEquals(2, isEvenCalled.get());
        assertEquals(1, parityCalled.get());
        assertEquals(2, textCalled.get());
        assertEquals(4, addToNumberCalled.get());

    }

    @Test
    public void nullStateValue() {
        MutableObservable<String> name = Observable.mutable(null);

        AtomicInteger helloWorldCalled = new AtomicInteger();
        Observable<String> helloWorld = Observable.compound(
                (list) -> {
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
                (list) -> {
                    isEvenCalled.addAndGet(1);
                    return ((int) list.get(0)) % 2 == 0;
                },
                List.of(num)
        );

        AtomicInteger parityCalled = new AtomicInteger();

        Observable<String> parity = Observable.compound(
                (list) -> {
                    parityCalled.addAndGet(1);
                    return ((boolean) list.get(0)) ? "even" : "odd";
                },
                List.of(isEven)
        );

        AtomicInteger textCalled = new AtomicInteger();

        Observable<String> text = Observable.compound(
                (list) -> {
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
        assertEquals(parity.get(), "even");
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        num.set(10);
        assertEquals(parity.get(), "even");
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, parityCalled.get());

        assertEquals(text.get(), "this is even");
        assertEquals(2, isEvenCalled.get());
        assertEquals(2, textCalled.get());

        num.set(11);
        assertEquals(text.get(), "this is odd");

        assertEquals(3, isEvenCalled.get());
        assertEquals(3, textCalled.get());
        num.set(15);

        assertEquals(text.get(), "this is odd");

        assertEquals(4, isEvenCalled.get());
        assertEquals(3, textCalled.get());

        assertEquals(parity.get(), "odd");
        assertEquals(4, isEvenCalled.get());
        assertEquals(3, textCalled.get());
    }

    @Test
    void testListState() {
        ListObservable<String> names = Observable.list();

        AtomicInteger stateCalled = new AtomicInteger();

        Observable<String> state = Observable.compound(
                (list) -> {
                    stateCalled.addAndGet(1);
                    List<String> namesList = (List<String>) list.get(0);

                    return "[" + String.join(", ", namesList) + "]";
                },
                List.of(names)
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
    }

    @Test
    void coolCompoundTest() {
        MutableObservable<Integer> number = Observable.mutable(9);

        AtomicInteger isEvenCalled = new AtomicInteger();

        Observable<Boolean> isEven = Observable.compound(number).map((record) -> {
            isEvenCalled.addAndGet(1);
            return record.arg0() % 2 == 0;
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
        Observable<String> parity = Observable.compound(isEven).map((record) -> {
            parityCalled.addAndGet(1);
            return record.arg0() ? "Even" : "Odd";
        });

        assertEquals("Even", parity.get());
        assertEquals(1, parityCalled.get());
        assertEquals(2, isEvenCalled.get());

        number.set(11);
        assertEquals("Odd", parity.get());
        assertEquals(2, parityCalled.get());
        assertEquals(3, isEvenCalled.get());

        AtomicInteger textCalled = new AtomicInteger();
        Observable<String> text = Observable.compound(number, parity).map((record) -> {
            textCalled.addAndGet(1);
            return record.arg0() + " is " + record.arg1();
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

        Observable<List<String>> loreState = players.map((list) -> {
            List<String> lore = new ArrayList<>();
            for (String player : list)
                lore.add(" - " + player);
            return lore;
        });

    }

    @Test
    void watcherTest() {
        MutableObservable<Integer> number = Observable.mutable(9);
        WatcherManager watcherManager = new WatcherManager();


        AtomicInteger current = new AtomicInteger(9);

        number.watch(watcherManager, (num) -> {
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
