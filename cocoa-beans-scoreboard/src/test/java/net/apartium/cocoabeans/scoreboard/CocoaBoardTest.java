package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.scoreboard.packet.DisplayPacket;
import net.apartium.cocoabeans.scoreboard.packet.ObjectivePacket;
import net.apartium.cocoabeans.scoreboard.packet.ScorePacket;
import net.apartium.cocoabeans.scoreboard.packet.TeamPacket;
import net.apartium.cocoabeans.state.MutableObservable;
import net.apartium.cocoabeans.state.Observable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
    void simpleHeartbeat() {
        MutableObservable<Component> title = Observable.mutable(Component.text("title"));
        MutableObservable<Component> text = Observable.mutable(Component.text("text"));
        MutableObservable<Component> score = Observable.mutable(Component.text("score"));
        boolean isCustomScoreSupported = true;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                title,
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );

        board.getPackets().clear();
        board.heartbeat();
        assertEquals(List.of(), board.getPackets());
        board.heartbeat();
        assertEquals(List.of(), board.getPackets());

        title.set(Component.text("newTitle"));
        assertEquals(List.of(), board.getPackets());

        board.heartbeat();
        assertEquals(List.of(
                new ObjectivePacket(ObjectiveMode.UPDATE, Component.text("newTitle"))
        ), board.getPackets());

        board.getPackets().clear();

        board.heartbeat();
        assertEquals(List.of(), board.getPackets());

        board.line(0, text, score);
        assertEquals(List.of(
                new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.heartbeat();
        assertEquals(List.of(), board.getPackets());

        text.set(Component.text("newText"));
        assertEquals(List.of(), board.getPackets());

        board.heartbeat();
        assertEquals(List.of(
                new TeamPacket(0, TeamMode.UPDATE, Component.text("newText"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.heartbeat();
        assertEquals(List.of(), board.getPackets());

        score.set(Component.text("newScore"));
        assertEquals(List.of(), board.getPackets());

        board.heartbeat();
        assertEquals(List.of(
                new ScorePacket(0, Component.text("newScore"), ScoreboardAction.CREATE_OR_UPDATE, null)
        ), board.getPackets());
        board.getPackets().clear();

        board.heartbeat();
        assertEquals(List.of(), board.getPackets());

    }

    @Test
    void deleteBoard() {
        boolean isCustomScoreSupported = false;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.line(0, Component.text("text"), Component.text("score"));
        assertEquals(List.of(
                new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.line(1, Component.text("anotherText"), null);
        assertEquals(List.of(
                new ScorePacket(1, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(1, TeamMode.CREATE, Component.text("text"), null),
                new TeamPacket(0, TeamMode.UPDATE, Component.text("anotherText"), null),
                new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null)
        ), board.getPackets());
        board.getPackets().clear();

        board.delete();
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new TeamPacket(1, TeamMode.REMOVE, null, null),
                        new ObjectivePacket(ObjectiveMode.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

    }

    @Test
    void setLine() {
        boolean isCustomScoreSupported = true;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.line(0, Component.text("text"));

        assertEquals(
                List.of(
                        new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.line(0, Component.text("changeText"));
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.UPDATE, Component.text("changeText"), null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.line(0, Component.text("anotherText"), Component.text("score"), Style.style(NamedTextColor.GOLD));
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.UPDATE, Component.text("anotherText"), null),
                        new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, Style.style(NamedTextColor.GOLD))
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.line(2, Component.text("low"));
        assertEquals(
                List.of(
                        new ScorePacket(1, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(1, TeamMode.CREATE, null, null),
                        new ScorePacket(2, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, Style.style(NamedTextColor.GOLD)),
                        new TeamPacket(2, TeamMode.CREATE, Component.text("anotherText"), null),
                        new TeamPacket(0, TeamMode.UPDATE, Component.text("low"), null),
                        new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.remove(0);
        board.remove(0);
        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(2, TeamMode.REMOVE, null, null),
                        new ScorePacket(2, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(1, TeamMode.REMOVE, null, null),
                        new ScorePacket(1, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();
    }

    @Test
    void addLine() {
        boolean isCustomScoreSupported = true;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("text"));
        assertEquals(List.of(
                new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("text"), Component.text("score"));
        assertEquals(List.of(
                new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("text"), Component.text("score"), Style.style(NamedTextColor.GOLD));
        assertEquals(List.of(
                new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, Style.style(NamedTextColor.GOLD)),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();
    }

    @Test
    void addLineOffset() {
        boolean isCustomScoreSupported = true;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("text"), 0);
        assertEquals(List.of(
                new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("text"), Component.text("score"), 0);
        assertEquals(List.of(
                new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, null),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("text"), Component.text("score"), Style.style(NamedTextColor.BLUE), 0);
        assertEquals(List.of(
                new ScorePacket(0, Component.text("score"), ScoreboardAction.CREATE_OR_UPDATE, Style.style(NamedTextColor.BLUE)),
                new TeamPacket(0, TeamMode.CREATE, Component.text("text"), null)
        ), board.getPackets());
        board.getPackets().clear();

        board.remove(0);
        assertEquals(
                List.of(
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();
    }


    @Test
    void toObservable() {
        assertNull(CocoaBoard.toObservable(null));
        assertEquals(Observable.empty(), CocoaBoard.toObservable(Component.empty()));
        assertEquals(Observable.immutable(Component.text("test")).get(), CocoaBoard.toObservable(Component.text("test")).get());
    }

    @Test
    void updateLines() {
        boolean isCustomScoreSupported = true;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.updateLines(List.of(
                Component.text("Line0"),
                Component.empty(),
                Component.text("Line2")
        ));
        assertEquals(
                List.of(
                        new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(0, TeamMode.CREATE, Component.text("Line2"), null),
                        new ScorePacket(1, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(1, TeamMode.CREATE, null, null),
                        new ScorePacket(2, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(2, TeamMode.CREATE, Component.text("Line0"), null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.updateLines(List.of());
        assertEquals(
                List.of(
                        new TeamPacket(2, TeamMode.REMOVE, null, null),
                        new ScorePacket(2, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(1, TeamMode.REMOVE, null, null),
                        new ScorePacket(1, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();


        board.updateLines(
                List.of(
                        Component.text("Line0"),
                        Component.empty(),
                        Component.text("Line2")
                ),
                List.of(
                        Component.text("0Line"),
                        Component.empty(),
                        Component.text("2Line")
                )
        );

        assertEquals(
                List.of(
                        new ScorePacket(0, Component.text("2Line"), ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(0, TeamMode.CREATE, Component.text("Line2"), null),
                        new ScorePacket(1, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(1, TeamMode.CREATE, null, null),
                        new ScorePacket(2, Component.text("0Line"), ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(2, TeamMode.CREATE, Component.text("Line0"), null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.updateLines(List.of());
        assertEquals(
                List.of(
                        new TeamPacket(2, TeamMode.REMOVE, null, null),
                        new ScorePacket(2, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(1, TeamMode.REMOVE, null, null),
                        new ScorePacket(1, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.updateLines(
                List.of(
                        Component.text("Line0"),
                        Component.empty(),
                        Component.text("Line2")
                ),
                List.of(
                        Component.text("0Line"),
                        Component.empty(),
                        Component.text("2Line")
                ),
                Arrays.asList(
                        null,
                        Style.style(NamedTextColor.GREEN),
                        null
                )
        );

        assertEquals(
                List.of(
                        new ScorePacket(0, Component.text("2Line"), ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(0, TeamMode.CREATE, Component.text("Line2"), null),
                        new ScorePacket(1, null, ScoreboardAction.CREATE_OR_UPDATE, Style.style(NamedTextColor.GREEN)),
                        new TeamPacket(1, TeamMode.CREATE, null, null),
                        new ScorePacket(2, Component.text("0Line"), ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(2, TeamMode.CREATE, Component.text("Line0"), null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.updateLines(List.of());
        assertEquals(
                List.of(
                        new TeamPacket(2, TeamMode.REMOVE, null, null),
                        new ScorePacket(2, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(1, TeamMode.REMOVE, null, null),
                        new ScorePacket(1, null, ScoreboardAction.REMOVE, null),
                        new TeamPacket(0, TeamMode.REMOVE, null, null),
                        new ScorePacket(0, null, ScoreboardAction.REMOVE, null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        assertThrows(IllegalArgumentException.class, () -> board.updateLines(List.of(Component.text("Line0")), List.of()));
        assertThrows(IllegalArgumentException.class, () -> board.updateLines(List.of(Component.text("Line0")), List.of(Component.text("0Line")), List.of()));
    }

    @Test
    void title() {
        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                false
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.title(Component.text("newTitle"));
        assertEquals(
                List.of(new ObjectivePacket(ObjectiveMode.UPDATE, Component.text("newTitle"))),
                board.getPackets()
        );
        board.getPackets().clear();
    }

    @Test
    void numberStyle() {
        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                false
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.add(Component.text("Line"));
        assertEquals(
                List.of(
                        new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, null),
                        new TeamPacket(0, TeamMode.CREATE, Component.text("Line"), null)
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.numberStyle(0, Style.style(NamedTextColor.GREEN));
        assertEquals(
                List.of(new ScorePacket(0, null, ScoreboardAction.CREATE_OR_UPDATE, Style.style(NamedTextColor.GREEN))),
                board.getPackets()
        );
        board.getPackets().clear();
    }

    @Test
    void intoTeamName() {
        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                true
        );

        for (int i = 0; i < 16; i++) {
            assertEquals(objectiveId + ":" + i, board.intoTeamName(i));
        }
    }

    @Test
    void setDisplay() {
        boolean isCustomScoreSupported = false;

        String objectiveId = "my-player-uuid";
        TestBoard board = new TestBoard(
                objectiveId,
                Observable.immutable(Component.text("title")),
                isCustomScoreSupported
        );

        assertEquals(
                List.of(
                        new ObjectivePacket(ObjectiveMode.CREATE, Component.text("title")),
                        new DisplayPacket()
                ),
                board.getPackets()
        );
        board.getPackets().clear();

        board.setDisplay();
        assertEquals(List.of(new DisplayPacket()), board.getPackets());
        board.getPackets().clear();
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
