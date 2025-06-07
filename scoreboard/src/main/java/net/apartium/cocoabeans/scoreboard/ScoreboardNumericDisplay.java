package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.state.CompoundRecords;
import net.apartium.cocoabeans.state.DirtyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

import java.util.*;

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

        this.groupWatcher = new DirtyWatcher<>(group.observePlayers());
        this.displaySlots = Collections.newSetFromMap(new IdentityHashMap<>());
        this.displayName = displayName.watch();
    }

    public BoardPlayerGroup<P> getGroup() {
        return group;
    }

    public void set(String entity, Observable<Integer> score, Observable<Component> fixedComponent, Observable<Style> style) {
        DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>> watcher = entities.put(
                entity,
                Observable.compound(
                        Optional.ofNullable(score).orElse(Observable.immutable(0)),
                        Optional.ofNullable(fixedComponent).orElse(Observable.immutable(null)),
                        Optional.ofNullable(style).orElse(Observable.immutable(null))
                ).watch()
        );

        System.out.println(watcher.get().getClass());

        if (watcher == null)
            return;

        CompoundRecords.RecordOf3<Integer, Component, Style> record = watcher.get().key();
        sendScorePacket(group.players(), entity, record.arg0(), ScoreboardAction.CREATE_OR_UPDATE,  record.arg1(), record.arg2());
    }

    public void heartbeat() {
        if (displayName.isDirty()) {
            Entry<Component, Boolean> entry = displayName.get();
            if (entry.value())
                sendObjectivePacket(group.players(), ObjectiveMode.UPDATE, entry.key());
        }

        for (Map.Entry<String, DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entry : entities.entrySet()) {
            if (!entry.getValue().isDirty())
                continue;

            Entry<?, Boolean> record = entry.getValue().get();
            if (!record.value())
                continue;

            if (!(record.key() instanceof CompoundRecords.RecordOf3<?, ?, ?> rec))
                throw new IllegalStateException("TF: " + record.key().getClass());

            sendScorePacket(
                    group.players(),
                    entry.getKey(),
                    (int) rec.arg0(),
                    ScoreboardAction.CREATE_OR_UPDATE,
                    (Component) rec.arg1(),
                    (Style) rec.arg2()
            );
        }
    }

    public void remove(String entity) {
        DirtyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>> record = entities.remove(entity);
        if (record == null)
            return;

        sendScorePacket(group.players(), entity, 0, ScoreboardAction.REMOVE, null, null);
        record.delete();
    }

    public void addDisplaySlot(DisplaySlot slot) {
        if (!displaySlots.add(slot))
            return;

        sendDisplayPacket(group.players(), slot, objectiveId);
    }

    public void removeDisplaySlot(DisplaySlot slot) {
        if (!displaySlots.remove(slot))
            return;

        sendDisplayPacket(group.players(), slot, null);
    }

    public void renderType(ObjectiveRenderType type) {
        if (renderType == type)
            return;

        renderType = type;
        sendObjectivePacket(group.players(), ObjectiveMode.UPDATE, displayName.get().key());
    }

    public void displayName(Observable<Component> displayName) {
        if (this.displayName != null)
            this.displayName.delete();

        this.displayName = displayName.watch();
    }

    public void delete() {
        sendObjectivePacket(group.players(), ObjectiveMode.REMOVE, null);

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
