package net.apartium.cocoabeans.scoreboard.team;

import net.apartium.cocoabeans.scoreboard.BoardPlayerGroup;
import net.apartium.cocoabeans.state.LazyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * DisplayTeam is Packet-level NMS-driven team display
 * that let you make custom Team easier with Observable fields
 * @param <P> type of player
 */
@ApiStatus.AvailableSince("0.0.41")
public abstract class DisplayTeam<P> {

    protected final String name;
    protected final LazyWatcher<Set<String>> entities = LazyWatcher.create(Observable.set());
    protected final BoardPlayerGroup<P> group;
    protected final LazyWatcher<Set<P>> groupWatcher;

    protected final LazyWatcher<Component> displayName = LazyWatcher.create(Observable.empty());

    protected final LazyWatcher<NameTagVisibilityRule> nameTagVisibilityRule = LazyWatcher.create(Observable.immutable(NameTagVisibilityRule.ALWAYS));
    protected final LazyWatcher<CollisionRule> collisionRule = LazyWatcher.create(Observable.immutable(CollisionRule.ALWAYS));

    protected final LazyWatcher<Component> prefix = LazyWatcher.create(Observable.empty());
    protected final LazyWatcher<Component> suffix = LazyWatcher.create(Observable.empty());

    protected final LazyWatcher<ChatFormatting> formatting = LazyWatcher.create(Observable.immutable(ChatFormatting.RESET));

    protected final LazyWatcher<Byte> friendlyFire = LazyWatcher.create(Observable.immutable((byte) 0x00));

    protected DisplayTeam(String name, BoardPlayerGroup<P> group) {
        this.name = name;
        this.group = group;

        this.groupWatcher = LazyWatcher.create(group.observePlayers());
    }

    protected Set<P> currentWatcher() {
        return Optional.ofNullable(groupWatcher.getLastValue())
                .orElse(Set.of());
    }

    protected boolean isDirty() {
        return displayName.isDirty()
                || nameTagVisibilityRule.isDirty()
                || collisionRule.isDirty()
                || prefix.isDirty()
                || suffix.isDirty()
                || formatting.isDirty()
                || friendlyFire.isDirty();
    }

    public void heartbeat() {
        heartbeatEntities();
        heartbeatSettings();
        heartbeatAudience();
    }

    /**
     * Send board updates to all viewers if its values has changed
     */
    private void heartbeatSettings() {
        if (!isDirty())
            return;

        boolean hadChange = false;
        Entry<?, Boolean> current = this.displayName.getOrUpdate();
        if (current.value())
            hadChange = true;

        Component displayName = (Component) current.key();

        current = this.nameTagVisibilityRule.getOrUpdate();
        if (current.value())
            hadChange = true;

        NameTagVisibilityRule nameTagVisibilityRule = (NameTagVisibilityRule) current.key();

        current = this.collisionRule.getOrUpdate();
        if (current.value())
            hadChange = true;

        CollisionRule collisionRule = (CollisionRule) current.key();

        current = this.prefix.getOrUpdate();
        if (current.value())
            hadChange = true;

        Component prefix = (Component) current.key();

        current = this.suffix.getOrUpdate();
        if (current.value())
            hadChange = true;

        Component suffix = (Component) current.key();

        current = this.formatting.getOrUpdate();
        if (current.value())
            hadChange = true;

        ChatFormatting formatting = (ChatFormatting) current.key();

        current = this.friendlyFire.getOrUpdate();
        if (current.value())
            hadChange = true;

        Byte friendlyFire = (Byte) current.key();

        if (!hadChange)
            return;

        sendUpdateTeamPacket(
                currentWatcher(),
                displayName,
                friendlyFire,
                nameTagVisibilityRule,
                collisionRule,
                formatting,
                prefix,
                suffix
        );
    }

    private void heartbeatAudience() {
        if (!groupWatcher.isDirty())
            return;

        Set<P> cache = groupWatcher.getLastValue();
        if (cache == null)
            cache = Set.of();

        Entry<Set<P>, Boolean> entry = groupWatcher.getOrUpdate();

        if (!entry.value())
            return;

        Set<P> toAdd = new HashSet<>(entry.key());
        toAdd.removeAll(cache);

        Set<P> toRemove = new HashSet<>(cache);
        toRemove.removeAll(entry.key());

        sendCreateTeamPacket(
                toAdd,
                displayName.getLastValue(),
                Optional.ofNullable(friendlyFire.getLastValue()).orElse((byte) 0x01),
                nameTagVisibilityRule.getLastValue(),
                collisionRule.getLastValue(),
                formatting.getLastValue(),
                prefix.getLastValue(),
                suffix.getLastValue(),
                Optional.ofNullable(entities.getLastValue()).orElse(Set.of())
        );

        sendRemoveTeamPacket(
                toRemove
        );
    }

    private void heartbeatEntities() {
        if (!entities.isDirty())
            return;

        Set<String> cacheEntities = entities.getLastValue();
        if (cacheEntities == null)
            cacheEntities = Set.of();

        Entry<Set<String>, Boolean> entry = entities.getOrUpdate();
        if (!entry.value())
            return;

        // Compare
        Set<String> toAdd = new HashSet<>(entry.key());
        toAdd.removeAll(cacheEntities);

        Set<String> toRemove = new HashSet<>(cacheEntities);
        toRemove.removeAll(entry.key());

        if (!toAdd.isEmpty())
            sendAddEntitiesPacket(currentWatcher(), toAdd);

        if (!toRemove.isEmpty())
            sendRemoveEntitiesPacket(currentWatcher(), toRemove);
    }

    /**
     * Get the group of players that is able to view this object
     * @return display audience
     */
    public BoardPlayerGroup<P> getViewers() {
        return group;
    }

    /**
     * Add an entity to the current team
     * @param entity entity name (such as player name)
     * @return current instance
     */
    public DisplayTeam<P> addEntity(String... entity) {
        ((SetObservable<String>) entities.getWatchedObservable()).addAll(Set.of(entity));
        return this;
    }

    /**
     * Get members of the current team
     * @return members of the current team
     */
    public SetObservable<String> getEntities() {
        return (SetObservable<String>) entities.getWatchedObservable();
    }

    /**
     * Remove an entity from the current team
     * @param entity entity name
     * @return current instance
     */
    public DisplayTeam<P> removeEntity(String... entity) {
        ((SetObservable<String>) entities.getWatchedObservable()).removeAll(Set.of(entity));
        return this;
    }

    /**
     * TODO what tf is that
     * Set display name of the current team
     * @param displayName display name
     * @return current instance
     */
    public DisplayTeam<P> setDisplayName(Observable<Component> displayName) {
        this.displayName.setDependsOn(displayName);
        return this;
    }

    /**
     * Set name tag visibility of the current team
     * @param nameTagVisibilityRule name tag visibility rule
     * @see NameTagVisibilityRule
     * @return current instance
     */
    public DisplayTeam<P> setNameTagVisibilityRule(Observable<NameTagVisibilityRule> nameTagVisibilityRule) {
        this.nameTagVisibilityRule.setDependsOn(nameTagVisibilityRule);
        return this;
    }

    /**
     * Set collision rule of the current team
     * @param collisionRule collision rule
     * @see CollisionRule
     * @return current instance
     */
    public DisplayTeam<P> setCollisionRule(Observable<CollisionRule> collisionRule) {
        this.collisionRule.setDependsOn(collisionRule);
        return this;
    }

    /**
     * Set the current team's prefix
     * @param prefix prefix observable
     * @return current instance
     */
    public DisplayTeam<P> setPrefix(Observable<Component> prefix) {
        this.prefix.setDependsOn(prefix);
        return this;
    }

    /**
     * Set the current team's suffix
     * @param suffix suffix observable
     * @return current instance
     */
    public DisplayTeam<P> setSuffix(Observable<Component> suffix) {
        this.suffix.setDependsOn(suffix);
        return this;
    }

    /**
     * Set the format of the current team
     * @param formatting formatting observable
     * @return current instance
     */
    public DisplayTeam<P> setFormatting(Observable<ChatFormatting> formatting) {
        this.formatting.setDependsOn(formatting);
        return this;
    }

    /**
     * Bit mask. 0b01: Allow friendly fire, 0b10: can see invisible entities on same team.
     * @param friendlyFire friendlyFire
     */
    public DisplayTeam<P> setFriendlyFire(Observable<Byte> friendlyFire) {
        this.friendlyFire.setDependsOn(friendlyFire);
        return this;
    }

    /**
     * Delete the current view, removing itself from all its viewers and clearing its own content
     */
    public void delete() {
        sendRemoveTeamPacket(currentWatcher());

        entities.delete();
        displayName.delete();
        nameTagVisibilityRule.delete();
        collisionRule.delete();
        prefix.delete();
        suffix.delete();
        formatting.delete();
        friendlyFire.delete();
    }

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public abstract void sendUpdateTeamPacket(
            Set<P> audience,
            Component displayName,
            byte friendlyFire,
            NameTagVisibilityRule nameTagVisibilityRule,
            CollisionRule collisionRule,
            ChatFormatting formatting,
            Component prefix,
            Component suffix
    );

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public abstract void sendAddEntitiesPacket(
            Set<P> audience,
            Collection<String> addEntities
    );

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public abstract void sendRemoveEntitiesPacket(
            Set<P> audience,
            Collection<String> removeEntities
    );

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public abstract void sendCreateTeamPacket(
            Set<P> audience,
            Component displayName,
            byte friendlyFire,
            NameTagVisibilityRule nameTagVisibilityRule,
            CollisionRule collisionRule,
            ChatFormatting formatting,
            Component prefix,
            Component suffix,
            Collection<String> entities
    );

    /**
     * @hidden
     */
    @ApiStatus.Internal
    public abstract void sendRemoveTeamPacket(Set<P> audience);

}
