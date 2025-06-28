package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.state.CompoundRecords;
import net.apartium.cocoabeans.state.DirtyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.AvailableSince("0.0.41")
public abstract class ScoreboardNumericDisplay<P> {

    protected final String objectiveId;

    protected final BoardPlayerGroup<P> group;
    protected final DirtyWatcher<Set<P>> groupWatcher;

    protected final Set<DisplaySlot> displaySlots;
    protected final Map<String, DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entities = new HashMap<>();
    protected DirtyWatcher<Component> displayName;
    protected ObjectiveRenderType renderType = ObjectiveRenderType.INTEGER;

    protected ScoreboardNumericDisplay(String objectiveId, BoardPlayerGroup<P> group, Observable<Component> displayName) {
        this.objectiveId = objectiveId;
        this.group = group;

        SetObservable<P> players = group.observePlayers();
        this.groupWatcher = DirtyWatcher.create(players);

        this.displaySlots = Collections.newSetFromMap(new IdentityHashMap<>());
        this.displayName = displayName.watch();
    }

    public BoardPlayerGroup<P> getGroup() {
        return group;
    }

    protected Set<P> currentWatcher() {
        return Optional.ofNullable(groupWatcher.getCache())
                .orElse(Set.of());
    }

    public void set(String entity, Observable<Integer> score, Observable<Component> fixedComponent, Observable<Style> style) {
        Observable<CompoundRecords.RecordOf3<Integer, Component, Style>> compound = Observable.compound(
                score == Observable.<Integer>empty()
                        ? Observable.immutable(0)
                        : Optional.ofNullable(score).orElse(Observable.immutable(0)),
                fixedComponent == Observable.<Component>empty()
                        ? Observable.immutable(null)
                        : Optional.ofNullable(fixedComponent).orElse(Observable.immutable(null)),
                style == Observable.<Style>empty()
                        ? Observable.immutable(null)
                        : Optional.ofNullable(style).orElse(Observable.immutable(Style.style(NamedTextColor.RED)))
        );
        DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>> watcher = compound.watch();

        entities.put(
                entity,
                watcher
        );

        CompoundRecords.RecordOf3<Integer, Component, Style> record = watcher.get().key();
        sendScorePacket(currentWatcher(), entity, record.arg0(), ScoreboardAction.CREATE_OR_UPDATE,  record.arg1(), record.arg2());
    }

    public void heartbeat() {
        if (displayName.isDirty()) {
            Entry<Component, Boolean> entry = displayName.get();
            if (entry.value())
                sendObjectivePacket(currentWatcher(), ObjectiveMode.UPDATE, Optional.ofNullable(entry.key()).orElse(Component.empty()));
        }

        for (Map.Entry<String, DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entry : entities.entrySet()) {
            if (!entry.getValue().isDirty())
                continue;

            Entry<CompoundRecords.RecordOf3<Integer, Component, Style>, Boolean> record = entry.getValue().get();
            if (!record.value())
                continue;

            sendScorePacket(
                    currentWatcher(),
                    entry.getKey(),
                    record.key().arg0(),
                    ScoreboardAction.CREATE_OR_UPDATE,
                    record.key().arg1(),
                    record.key().arg2()
            );
        }
        handleNewAudience();
    }

    private void handleNewAudience() {
        if (groupWatcher.isDirty()) {
            Set<P> cache = Optional.ofNullable(groupWatcher.getCache()).orElse(Collections.emptySet());
            Entry<Set<P>, Boolean> entry = groupWatcher.get();

            if (!entry.value())
                return;

            Set<P> toAdd = new HashSet<>(entry.key());
            toAdd.removeAll(cache);

            Set<P> toRemove = new HashSet<>(cache);
            toRemove.removeAll(entry.key());

            // Add

            sendObjectivePacket(
                    toAdd,
                    ObjectiveMode.CREATE,
                    Optional.ofNullable(displayName.getCache()).orElse(Component.empty())
            );

            for (DisplaySlot slot : displaySlots)
                sendDisplayPacket(
                        toAdd,
                        slot,
                        objectiveId
                );

            for (Map.Entry<String, DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entity : entities.entrySet()) {
                sendScorePacket(
                        toAdd,
                        entity.getKey(),
                        entity.getValue().getCache().arg0(),
                        ScoreboardAction.CREATE_OR_UPDATE,
                        entity.getValue().getCache().arg1(),
                        entity.getValue().getCache().arg2()
                );
            }

            // Remove

            sendObjectivePacket(
                    toRemove,
                    ObjectiveMode.REMOVE,
                    null
            );
        }
    }

    public void remove(String entity) {
        DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>> record = entities.remove(entity);
        if (record == null)
            return;

        sendScorePacket(currentWatcher(), entity, 0, ScoreboardAction.REMOVE, null, null);
        record.delete();
    }

    public void addDisplaySlot(DisplaySlot slot) {
        if (!displaySlots.add(slot))
            return;

        sendDisplayPacket(currentWatcher(), slot, objectiveId);
    }

    public void removeDisplaySlot(DisplaySlot slot) {
        if (!displaySlots.remove(slot))
            return;

        sendDisplayPacket(currentWatcher(), slot, null);
    }

    public void renderType(ObjectiveRenderType type) {
        if (renderType == type)
            return;

        renderType = type;
        sendObjectivePacket(currentWatcher(), ObjectiveMode.UPDATE, Optional.ofNullable(displayName.get().key()).orElse(Component.empty()));
    }

    public void displayName(Observable<Component> displayName) {
        if (this.displayName != null)
            this.displayName.delete();

        this.displayName = displayName.watch();
    }

    public void delete() {
        sendObjectivePacket(currentWatcher(), ObjectiveMode.REMOVE, null);

        for (DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>> value : entities.values())
            value.delete();

        entities.clear();

        displaySlots.clear();
        displayName.delete();
        groupWatcher.delete();
    }

    public abstract void sendDisplayPacket(Set<P> audience, DisplaySlot slot, String objectiveId);
    public abstract void sendScorePacket(Set<P> audience, String entity, int score, ScoreboardAction action, Component fixedContent, Style numberStyle);
    public abstract void sendObjectivePacket(Set<P> audience, ObjectiveMode mode, Component displayName);

}
