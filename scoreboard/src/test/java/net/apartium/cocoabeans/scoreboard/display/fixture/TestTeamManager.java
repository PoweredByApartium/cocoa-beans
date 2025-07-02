package net.apartium.cocoabeans.scoreboard.display.fixture;

import net.apartium.cocoabeans.scoreboard.fixture.TestViewerGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TestTeamManager {

    private final Map<String, TestDisplayTeam> teams = new HashMap<>();

    public void heartbeat() {
        for (TestDisplayTeam team : teams.values())
            team.heartbeat();
    }

    public TestDisplayTeam getTeam(String name) {
        return teams.computeIfAbsent(
                name,
                key -> new TestDisplayTeam(
                        key,
                        new TestViewerGroup(new HashSet<>())
                )
        );
    }



}
