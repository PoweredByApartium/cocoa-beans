package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.scoreboard.packet.DisplayPacket;
import net.apartium.cocoabeans.scoreboard.packet.ObjectivePacket;
import net.apartium.cocoabeans.scoreboard.packet.ScorePacket;
import net.apartium.cocoabeans.scoreboard.packet.TeamPacket;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CocoaBoardTest {

    @Test
    void simpleConstructor() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
    }


    @Test
    void noRandomPacketSending() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        for (int i = 0; i < 1024; i++) {
            board.heartbeat();
            assertEquals(
                    List.of(
                            new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                            new DisplayPacket()
                    ),
                    board.getPackets()
            );
        }
    }

    @Test
    void simpleOneLineAndChange() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line = Observable.immutable(Component.text("Line 0"));
        board.line(0, line);

        assertEquals(
                List.of(
                        new ScorePacket(
                                0,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        line = Observable.immutable(Component.text("Line 1"));
        board.line(0, line);

        assertEquals(
                List.of(
                        new TeamPacket(
                                0,
                                TeamMode.UPDATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );
    }

    @Test
    void simpleOneLineAddAndChange() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line = Observable.immutable(Component.text("Line 0"));
        board.add(line);

        assertEquals(
                List.of(
                        new ScorePacket(
                                0,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        line = Observable.immutable(Component.text("Line 1"));
        board.line(0, line);

        assertEquals(
                List.of(
                        new TeamPacket(
                                0,
                                TeamMode.UPDATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );
    }


    @Test
    void simpleMultipleLines() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line = Observable.immutable(Component.text("Line 0"));
        board.add(line);

        assertEquals(
                List.of(
                        new ScorePacket(
                                0,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line1 = Observable.immutable(Component.text("Line 1"));
        board.add(line1);

        assertEquals(
                List.of(
                        new ScorePacket(
                                1,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                1,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.UPDATE,
                                line1.get(),
                                null
                        )
                ),
                board.getPackets()
        );


        board.getPackets().clear();

        board.add(line1);
        assertEquals(
                List.of(
                        new ScorePacket(
                                2,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                2,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        ),
                        new TeamPacket(
                                1,
                                TeamMode.UPDATE,
                                line1.get(),
                                null
                        )
                ),
                board.getPackets()
        );
    }


    @Test
    void addAndRemoveMultipleLines() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line = Observable.immutable(Component.text("Line 0"));
        board.add(line);

        assertEquals(
                List.of(
                        new ScorePacket(
                                0,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line1 = Observable.immutable(Component.text("Line 1"));
        board.add(line1);

        assertEquals(
                List.of(
                        new ScorePacket(
                                1,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                1,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.UPDATE,
                                line1.get(),
                                null
                        )
                ),
                board.getPackets()
        );


        board.getPackets().clear();

        board.add(line1);
        assertEquals(
                List.of(
                        new ScorePacket(
                                2,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                2,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        ),
                        new TeamPacket(
                                1,
                                TeamMode.UPDATE,
                                line1.get(),
                                null
                        )
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        board.remove(1);
        assertEquals(
                List.of(
                        new TeamPacket(
                                2,
                                TeamMode.REMOVE,
                                null,
                                null
                        ),
                        new ScorePacket(
                                2,
                                null,
                                ScoreboardAction.REMOVE,
                                null
                        ),
                        new TeamPacket(
                                1,
                                TeamMode.UPDATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );
    }

    @Test
    void simpleOneLine() {
        String objectiveId = "my-player-uuid";
        Observable<Component> title = Observable.immutable(Component.text("Testing"));
        boolean isCustomScoreSupported = true;

        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, title.get()),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        board.getPackets().clear();

        Observable<Component> line = Observable.immutable(Component.text("Line 0"));
        board.line(0, line);

        assertEquals(
                List.of(
                        new ScorePacket(
                                0,
                                null,
                                ScoreboardAction.CREATE_OR_UPDATE,
                                null
                        ),
                        new TeamPacket(
                                0,
                                TeamMode.CREATE,
                                line.get(),
                                null
                        )
                ),
                board.getPackets()
        );
    }

}
