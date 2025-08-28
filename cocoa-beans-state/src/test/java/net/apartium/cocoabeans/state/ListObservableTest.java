package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListObservableTest {

    @Test
    void size() {
        ListObservable<Integer> scores = Observable.list();
        Observable<Integer> size = scores.size();

        assertEquals(0, size.get());
        assertEquals(List.of(), scores.get());

        scores.add(901);
        assertEquals(List.of(901), scores.get());
        assertEquals(1, size.get());

        scores.remove((Integer) 102);

        assertEquals(List.of(901), scores.get());
        assertEquals(1, size.get());
    }

    @Test
    void removeObserver() {
        ListObservable<Integer> scores = Observable.list();
        Observer observer = n -> {};
        scores.observe(observer);
        scores.add(100);
        assertTrue(scores.removeObserver(observer));
    }

}
