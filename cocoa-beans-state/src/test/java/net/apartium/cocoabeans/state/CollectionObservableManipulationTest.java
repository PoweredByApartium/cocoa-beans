package net.apartium.cocoabeans.state;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CollectionObservableManipulationTest {

    @Test
    void mapEachFilterFlatMapEachShouldChainCorrectly() {
        MutableObservable<String> kfirDisplayName = Observable.mutable("Kfir");
        MutableObservable<String> apartiumDisplayName = Observable.mutable("Apartium");
        MutableObservable<String> voigonDisplayName = Observable.mutable("voigon");

        GamePlayer kfir = new GamePlayer(UUID.randomUUID(), "kfir", kfirDisplayName, Observable.immutable(true));
        GamePlayer apartium = new GamePlayer(UUID.randomUUID(), "apartium", apartiumDisplayName, Observable.immutable(true));
        GamePlayer voigon = new GamePlayer(UUID.randomUUID(), "voigon", voigonDisplayName, Observable.immutable(false));

        ListObservable<GamePlayer> players = Observable.list(new ArrayList<>(List.of(
                kfir,
                apartium,
                voigon
        )));

        ListObservable<String> displayNames = players
                .mapEach(player -> new PlayerView(
                        player.name(),
                        player.displayName(),
                        player.alive()
                ))
                .filter(PlayerView::alive)
                .flatMapEach(PlayerView::displayName);

        assertEquals(List.of("Kfir", "Apartium"), displayNames.get());

        kfirDisplayName.set("Kfir2");

        assertEquals(List.of("Kfir2", "Apartium"), displayNames.get());

        voigonDisplayName.set("voigon2");

        assertEquals(List.of("Kfir2", "Apartium"), displayNames.get());

        apartiumDisplayName.set("Apartium2");

        assertEquals(List.of("Kfir2", "Apartium2"), displayNames.get());
    }

    private record GamePlayer(
            UUID uuid,
            String name,
            MutableObservable<String> displayName,
            Observable<Boolean> alive
    ) {}

    private record PlayerView(
            String name,
            Observable<String> displayName,
            Observable<Boolean> alive
    ) {

    }
    
}
