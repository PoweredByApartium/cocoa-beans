package net.apartium.cocoabeans.scoreboard.numeric;

import net.apartium.cocoabeans.scoreboard.numeric.fixture.ScoreboardNumericManager;
import org.junit.jupiter.api.BeforeEach;

class NumericDisplayBase {

    ScoreboardNumericManager manager;

    @BeforeEach
    public void setup() {
        manager = new ScoreboardNumericManager();
    }

    public void heartbeat() {
        manager.heartbeat();
    }

}
