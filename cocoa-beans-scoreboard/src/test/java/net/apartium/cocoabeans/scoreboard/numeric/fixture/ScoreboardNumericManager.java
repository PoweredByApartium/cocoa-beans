package net.apartium.cocoabeans.scoreboard.numeric.fixture;

import net.apartium.cocoabeans.fixture.TestViewerGroup;
import net.apartium.cocoabeans.state.Observable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ScoreboardNumericManager {

    private final Map<String, TestNumericDisplay> displays = new HashMap<>();

    public TestNumericDisplay getDisplay(String id) {
        return displays.computeIfAbsent(
                id,
                key -> new TestNumericDisplay(
                        key,
                        new TestViewerGroup(new HashSet<>()),
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
