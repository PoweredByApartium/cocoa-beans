package net.apartium.cocoabeans.scoreboard.numeric.fixture;

import net.apartium.cocoabeans.scoreboard.fixture.TestBoardPlayerGroup;
import net.apartium.cocoabeans.state.Observable;

import java.util.*;

public class ScoreboardNumericManager {

    private final Map<String, TestNumericDisplay> displays = new HashMap<>();

    public TestNumericDisplay getDisplay(String id) {
        return displays.computeIfAbsent(
                id,
                key -> new TestNumericDisplay(
                        key,
                        new TestBoardPlayerGroup(new HashSet<>()),
                        Observable.empty()
                )
        );
    }

    public void delete(String id) {
        TestNumericDisplay display = displays.remove(id);
        if (display != null)
            display.delete();
    }

    public void heartbeat() {
        displays.values().forEach(TestNumericDisplay::heartbeat);
    }

}
