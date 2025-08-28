package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.state.CompoundRecords;
import net.apartium.cocoabeans.state.LazyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Represents a numeric display in game scoreboard (hearts / score)
 * @see ObjectiveRenderType
 * @see DisplaySlot
 * @param <P>
 */
@ApiStatus.AvailableSince("0.0.41")
public abstract class ScoreboardNumericDisplay<P> {

    protected final String objectiveId;

    protected final ViewerGroup<P> group;
    protected final LazyWatcher<Set<P>> groupWatcher;

    protected final Set<DisplaySlot> displaySlots;
    protected final Map<String, LazyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entities = new HashMap<>();
    protected final LazyWatcher<Component> displayName;
    protected ObjectiveRenderType renderType = ObjectiveRenderType.INTEGER;

    protected ScoreboardNumericDisplay(String objectiveId, ViewerGroup<P> group, Observable<Component> displayName) {
        this.objectiveId = objectiveId;
        this.group = group;

        SetObservable<P> players = group.observePlayers();
        this.groupWatcher = LazyWatcher.create(players);

        this.displaySlots = Collections.newSetFromMap(new IdentityHashMap<>());
        this.displayName = displayName.lazyWatch();
    }

    /**
     * Get the group of players that is able to view this object
     * @return display audience
     */
    public ViewerGroup<P> getViewers() {
        return group;
    }

    protected Set<P> currentWatcher() {
        return Optional.ofNullable(groupWatcher.getLastValue())
                .orElse(Set.of());
    }

    /**
     * Adds an entity to this display
     * @param entity entity name (such as player name)
     * @param score score observable
     * @param suffix observable of text to come after the score
     * @param style text style observable
     */
    public void set(String entity, Observable<Integer> score, Observable<Component> suffix, Observable<Style> style) {
        var compound = Observable.compound(
                score == Observable.<Integer>empty()
                        ? Observable.immutable(0)
                        : Optional.ofNullable(score).orElse(Observable.immutable(0)),
                suffix == Observable.<Component>empty()
                        ? Observable.immutable(null)
                        : Optional.ofNullable(suffix).orElse(Observable.immutable(null)),
                style == Observable.<Style>empty()
                        ? Observable.immutable(null)
                        : Optional.ofNullable(style).orElse(Observable.immutable(Style.style(NamedTextColor.RED)))
        );
        var watcher = compound.lazyWatch();

        entities.put(
                entity,
                watcher
        );

        var record = watcher.getOrUpdate().key();
        sendScorePacket(currentWatcher(), entity, record.arg0(), ScoreboardAction.CREATE_OR_UPDATE,  record.arg1(), record.arg2());
    }

    /**
     * Send board updates to all viewers if its values has changed
     */
    public void heartbeat() {
        if (displayName.isDirty()) {
            Entry<Component, Boolean> entry = displayName.getOrUpdate();
            if (entry.value())
                sendObjectivePacket(currentWatcher(), ObjectiveMode.UPDATE, Optional.ofNullable(entry.key()).orElse(Component.empty()));
        }

        for (Map.Entry<String, LazyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entry : entities.entrySet()) {
            if (!entry.getValue().isDirty())
                continue;

            Entry<CompoundRecords.RecordOf3<Integer, Component, Style>, Boolean> record = entry.getValue().getOrUpdate();
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
            Set<P> cache = Optional.ofNullable(groupWatcher.getLastValue()).orElse(Collections.emptySet());
            Entry<Set<P>, Boolean> entry = groupWatcher.getOrUpdate();

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
                    Optional.ofNullable(displayName.getLastValue()).orElse(Component.empty())
            );

            for (DisplaySlot slot : displaySlots)
                sendDisplayPacket(
                        toAdd,
                        slot,
                        objectiveId
                );

            for (Map.Entry<String, LazyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>>> entity : entities.entrySet()) {
                CompoundRecords.RecordOf3<Integer, Component, Style> record = entity.getValue().getLastValue();

                sendScorePacket(
                        toAdd,
                        entity.getKey(),
                        record.arg0(),
                        ScoreboardAction.CREATE_OR_UPDATE,
                        record.arg1(),
                        record.arg2()
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

    /**
     * Remove an entity from the board
     * @param entity entity name to remove
     */
    public void remove(String entity) {
        var record = entities.remove(entity);
        if (record == null)
            return;

        sendScorePacket(currentWatcher(), entity, 0, ScoreboardAction.REMOVE, null, null);
        record.delete();
    }

    /**
     * Add display slot to the current display
     * @param slot slot to add
     */
    public void addDisplaySlot(DisplaySlot slot) {
        if (!displaySlots.add(slot))
            return;

        sendDisplayPacket(currentWatcher(), slot, objectiveId);
    }

    /**
     * Remove a display slot from the current display
     * @param slot slot to remove
     */
    public void removeDisplaySlot(DisplaySlot slot) {
        if (!displaySlots.remove(slot))
            return;

        sendDisplayPacket(currentWatcher(), slot, null);
    }

    /**
     * Sets the render type for the current display
     * @param type new render type
     */
    public void renderType(ObjectiveRenderType type) {
        if (renderType == type)
            return;

        renderType = type;
        sendObjectivePacket(currentWatcher(), ObjectiveMode.UPDATE, Optional.ofNullable(displayName.getOrUpdate().key()).orElse(Component.empty()));
    }

    /**
     * Sets the display name observable of the current display
     * @param displayName new display name observable
     */
    public void displayName(Observable<Component> displayName) {
        this.displayName.setDependsOn(displayName);
    }

    /**
     * Delete the current view, removing itself from all its viewers and clearing its own content
     */
    public void delete() {
        sendObjectivePacket(currentWatcher(), ObjectiveMode.REMOVE, null);

        for (LazyWatcher<CompoundRecords.RecordOf3<Integer, Component, Style>> value : entities.values())
            value.delete();

        entities.clear();

        displaySlots.clear();
        displayName.delete();
        groupWatcher.delete();
    }

    protected abstract void sendDisplayPacket(Set<P> audience, DisplaySlot slot, String objectiveId);

    protected abstract void sendScorePacket(Set<P> audience, String entity, int score, ScoreboardAction action, Component fixedContent, Style numberStyle);

    protected abstract void sendObjectivePacket(Set<P> audience, ObjectiveMode mode, Component displayName);

}
