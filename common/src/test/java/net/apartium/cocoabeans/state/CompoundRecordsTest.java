package net.apartium.cocoabeans.state;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

class CompoundRecordsTest {

    @Test
    void empty() {
        Observable<CompoundRecords.EmptyRecord> empty = CompoundRecords.compound();
        assertEquals(CompoundRecords.EmptyRecord.class, empty.get().getClass());
    }

    @Test
    void one() {
        MutableObservable<Integer> a = Observable.mutable(1);

        Observable<CompoundRecords.RecordOf1<Integer>> one = Observable.compound(a);
        assertEquals(CompoundRecords.RecordOf1.class, one.get().getClass());
        assertEquals(1, one.get().arg0());

        a.set(5);

        assertEquals(5, one.get().arg0());
        assertEquals(5, one.get().arg0());

        ((ObservableCompound<CompoundRecords.RecordOf1<Integer>>) one).flagAsDirty(null);
    }

    @Test
    void dirtyAndFirst() {
        MutableObservable<Integer> number = Observable.mutable(123);

        Observable<CompoundRecords.RecordOf1<Integer>> compound = Observable.compound(number);

        number.set(5);

        assertEquals(5, compound.get().arg0());
        assertEquals(5, compound.get().arg0());
    }

    @Test
    void fakeCollectionTest() {
        Collection<Integer> collection = new Collection<>() {
            @Override
            public int size() {
                return 3;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NotNull
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int c = 1;

                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Integer next() {
                        return c++;
                    }
                };
            }


            @Override
            public Object @NotNull [] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T @NotNull [] toArray(T @NotNull [] ts) {
                return ts;
            }

            @Override
            public boolean add(Integer integer) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NotNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(@NotNull Collection<? extends Integer> collection) {
                return false;
            }

            @Override
            public boolean removeAll(@NotNull Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(@NotNull Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {
                /*
                 * just for test
                 */
            }
        };

        MutableObservable<Collection<Integer>> mutable = Observable.mutable(collection);

        Observable<CompoundRecords.RecordOf1<Collection<Integer>>> compound = Observable.compound(mutable);

        assertEquals(collection, compound.get().arg0());

        mutable.set(List.of(3, 2, 4));
        assertEquals(List.of(3, 2, 4), compound.get().arg0());

        mutable.set(List.of(3, 2, 4));
        assertEquals(List.of(3, 2, 4), compound.get().arg0());
    }

    @Test
    void two() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);

        Observable<CompoundRecords.RecordOf2<Integer, Integer>> two = Observable.compound(a, b);
        assertEquals(CompoundRecords.RecordOf2.class, two.get().getClass());
        assertEquals(1, two.get().arg0());
        assertEquals(2, two.get().arg1());
    }

    @Test
    void three() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);

        Observable<CompoundRecords.RecordOf3<Integer, Integer, Integer>> three = Observable.compound(a, b, c);
        assertEquals(CompoundRecords.RecordOf3.class, three.get().getClass());
        assertEquals(1, three.get().arg0());
        assertEquals(2, three.get().arg1());
        assertEquals(3, three.get().arg2());
    }

    @Test
    void four() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);

        Observable<CompoundRecords.RecordOf4<Integer, Integer, Integer, Integer>> four = Observable.compound(a, b, c, d);
        assertEquals(CompoundRecords.RecordOf4.class, four.get().getClass());
        assertEquals(1, four.get().arg0());
        assertEquals(2, four.get().arg1());
        assertEquals(3, four.get().arg2());
        assertEquals(4, four.get().arg3());
    }

    @Test
    void five() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);
        MutableObservable<Integer> e = Observable.mutable(5);

        Observable<CompoundRecords.RecordOf5<Integer, Integer, Integer, Integer, Integer>> five = Observable.compound(a, b, c, d, e);
        assertEquals(CompoundRecords.RecordOf5.class, five.get().getClass());
        assertEquals(1, five.get().arg0());
        assertEquals(2, five.get().arg1());
        assertEquals(3, five.get().arg2());
        assertEquals(4, five.get().arg3());
        assertEquals(5, five.get().arg4());
    }

    @Test
    void six() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);
        MutableObservable<Integer> e = Observable.mutable(5);
        MutableObservable<Integer> f = Observable.mutable(6);

        Observable<CompoundRecords.RecordOf6<Integer, Integer, Integer, Integer, Integer, Integer>> six = Observable.compound(a, b, c, d, e, f);
        assertEquals(CompoundRecords.RecordOf6.class, six.get().getClass());
        assertEquals(1, six.get().arg0());
        assertEquals(2, six.get().arg1());
        assertEquals(3, six.get().arg2());
        assertEquals(4, six.get().arg3());
        assertEquals(5, six.get().arg4());
        assertEquals(6, six.get().arg5());
    }

    @Test
    void seven() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);
        MutableObservable<Integer> e = Observable.mutable(5);
        MutableObservable<Integer> f = Observable.mutable(6);
        MutableObservable<Integer> g = Observable.mutable(7);

        Observable<CompoundRecords.RecordOf7<Integer, Integer, Integer, Integer, Integer, Integer, Integer>> seven = Observable.compound(a, b, c, d, e, f, g);
        assertEquals(CompoundRecords.RecordOf7.class, seven.get().getClass());
        assertEquals(1, seven.get().arg0());
        assertEquals(2, seven.get().arg1());
        assertEquals(3, seven.get().arg2());
        assertEquals(4, seven.get().arg3());
        assertEquals(5, seven.get().arg4());
        assertEquals(6, seven.get().arg5());
        assertEquals(7, seven.get().arg6());
    }

    @Test
    void eight() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);
        MutableObservable<Integer> e = Observable.mutable(5);
        MutableObservable<Integer> f = Observable.mutable(6);
        MutableObservable<Integer> g = Observable.mutable(7);
        MutableObservable<Integer> h = Observable.mutable(8);

        Observable<CompoundRecords.RecordOf8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> eight = Observable.compound(a, b, c, d, e, f, g, h);
        assertEquals(CompoundRecords.RecordOf8.class, eight.get().getClass());
        assertEquals(1, eight.get().arg0());
        assertEquals(2, eight.get().arg1());
        assertEquals(3, eight.get().arg2());
        assertEquals(4, eight.get().arg3());
        assertEquals(5, eight.get().arg4());
        assertEquals(6, eight.get().arg5());
        assertEquals(7, eight.get().arg6());
        assertEquals(8, eight.get().arg7());
    }

    @Test
    void nine() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);
        MutableObservable<Integer> e = Observable.mutable(5);
        MutableObservable<Integer> f = Observable.mutable(6);
        MutableObservable<Integer> g = Observable.mutable(7);
        MutableObservable<Integer> h = Observable.mutable(8);
        MutableObservable<Integer> i = Observable.mutable(9);

        Observable<CompoundRecords.RecordOf9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> nine = Observable.compound(a, b, c, d, e, f, g, h, i);
        assertEquals(CompoundRecords.RecordOf9.class, nine.get().getClass());
        assertEquals(1, nine.get().arg0());
        assertEquals(2, nine.get().arg1());
        assertEquals(3, nine.get().arg2());
        assertEquals(4, nine.get().arg3());
        assertEquals(5, nine.get().arg4());
        assertEquals(6, nine.get().arg5());
        assertEquals(7, nine.get().arg6());
        assertEquals(8, nine.get().arg7());
        assertEquals(9, nine.get().arg8());
    }

    @Test
    void ten() {
        MutableObservable<Integer> a = Observable.mutable(1);
        MutableObservable<Integer> b = Observable.mutable(2);
        MutableObservable<Integer> c = Observable.mutable(3);
        MutableObservable<Integer> d = Observable.mutable(4);
        MutableObservable<Integer> e = Observable.mutable(5);
        MutableObservable<Integer> f = Observable.mutable(6);
        MutableObservable<Integer> g = Observable.mutable(7);
        MutableObservable<Integer> h = Observable.mutable(8);
        MutableObservable<Integer> i = Observable.mutable(9);
        MutableObservable<Integer> j = Observable.mutable(10);

        Observable<CompoundRecords.RecordOf10<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer>> ten = Observable.compound(a, b, c, d, e, f, g, h, i, j);
        assertEquals(CompoundRecords.RecordOf10.class, ten.get().getClass());
        assertEquals(1, ten.get().arg0());
        assertEquals(2, ten.get().arg1());
        assertEquals(3, ten.get().arg2());
        assertEquals(4, ten.get().arg3());
        assertEquals(5, ten.get().arg4());
        assertEquals(6, ten.get().arg5());
        assertEquals(7, ten.get().arg6());
        assertEquals(8, ten.get().arg7());
        assertEquals(9, ten.get().arg8());
        assertEquals(10, ten.get().arg9());
    }

    @Test
    void tooLarge() {
        Observable<Integer> a = Observable.immutable(1);
        Observable<Integer> b = Observable.immutable(2);
        Observable<Integer> c = Observable.immutable(3);
        Observable<Integer> d = Observable.immutable(4);
        Observable<Integer> e = Observable.immutable(5);
        Observable<Integer> f = Observable.immutable(6);
        Observable<Integer> g = Observable.immutable(7);
        Observable<Integer> h = Observable.immutable(8);
        Observable<Integer> i = Observable.immutable(9);
        Observable<Integer> j = Observable.immutable(10);
        Observable<Integer> k = Observable.immutable(11);
        Observable<Integer> l = Observable.immutable(12);
        Observable<Integer> m = Observable.immutable(13);
        Observable<Integer> n = Observable.immutable(14);
        Observable<Integer> o = Observable.immutable(15);
        Observable<Integer> p = Observable.immutable(16);
        Observable<Integer> q = Observable.immutable(17);
        Observable<Integer> r = Observable.immutable(18);
        Observable<Integer> s = Observable.immutable(19);
        Observable<Integer> t = Observable.immutable(20);

        Observable<CompoundRecords.CompoundRecord> compound = CompoundRecords.compound(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t);

        assertThrowsExactly(IndexOutOfBoundsException.class, compound::get);
    }

}
