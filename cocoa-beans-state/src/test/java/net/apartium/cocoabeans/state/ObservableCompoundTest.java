package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ObservableCompoundTest {

    enum TeamStatus {
        ALIVE,
        RESPAWNING
    }

    @Test
    void compoundAcceptsListOfObservable() {
        Observable<TeamStatus> status = Observable.immutable(TeamStatus.ALIVE);

        Observable<Boolean> result = Observable.compound(
                values -> values.stream()
                        .map(TeamStatus.class::cast)
                        .allMatch(s -> s != TeamStatus.RESPAWNING),
                List.of(status)
        );

        assertNotNull(result);
    }

    @Test
    void compoundAcceptsListOfMutableObservableSubtype() {
        MutableObservable<TeamStatus> status = Observable.mutable(TeamStatus.ALIVE);

        Observable<Boolean> result = Observable.compound(
                values -> values.stream()
                        .map(TeamStatus.class::cast)
                        .allMatch(s -> s != TeamStatus.RESPAWNING),
                List.of(status)
        );

        assertNotNull(result);
    }

    @Test
    void compoundAcceptsStreamToListOfMutableObservableSubtype() {
        List<MutableObservable<TeamStatus>> depends = List.of(
                Observable.mutable(TeamStatus.ALIVE),
                Observable.mutable(TeamStatus.ALIVE)
        );

        Observable<Boolean> result = Observable.compound(
                values -> values.stream()
                        .map(TeamStatus.class::cast)
                        .allMatch(s -> s != TeamStatus.RESPAWNING),
                depends
        );

        assertNotNull(result);
    }

    @Test
    void compoundComputesFalseWhenAnyTeamRespawning() {
        List<MutableObservable<TeamStatus>> depends = List.of(
                Observable.mutable(TeamStatus.ALIVE),
                Observable.mutable(TeamStatus.RESPAWNING)
        );

        Observable<Boolean> result = Observable.compound(
                values -> values.stream()
                        .map(TeamStatus.class::cast)
                        .allMatch(s -> s != TeamStatus.RESPAWNING),
                depends
        );

        assertFalse(result.get());
    }
}