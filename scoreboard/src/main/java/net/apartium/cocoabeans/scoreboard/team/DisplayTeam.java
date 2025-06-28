package net.apartium.cocoabeans.scoreboard.team;

import net.apartium.cocoabeans.scoreboard.BoardPlayerGroup;
import net.apartium.cocoabeans.state.DirtyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import net.apartium.cocoabeans.state.SetObservable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.AvailableSince("0.0.41")
public abstract class DisplayTeam<P> {

    protected final String name;
    protected final DirtyWatcher<Set<String>> entities = DirtyWatcher.create(Observable.set());
    protected final BoardPlayerGroup<P> group;
    protected final DirtyWatcher<Set<P>> groupWatcher;

    protected DirtyWatcher<Component> displayName = DirtyWatcher.create(Observable.empty());

    protected DirtyWatcher<NameTagVisibilityRule> nameTagVisibilityRule = DirtyWatcher.create(Observable.immutable(NameTagVisibilityRule.ALWAYS));
    protected DirtyWatcher<CollisionRule> collisionRule = DirtyWatcher.create(Observable.immutable(CollisionRule.ALWAYS));

    protected DirtyWatcher<Component> prefix = DirtyWatcher.create(Observable.empty());
    protected DirtyWatcher<Component> suffix = DirtyWatcher.create(Observable.empty());

    protected DirtyWatcher<ChatFormatting> formatting = DirtyWatcher.create(Observable.immutable(ChatFormatting.RESET));

    protected DirtyWatcher<Byte> friendlyFire = DirtyWatcher.create(Observable.immutable((byte) 0x00));

    public DisplayTeam(String name, BoardPlayerGroup<P> group) {
        this.name = name;
        this.group = group;

        this.groupWatcher = DirtyWatcher.create(group.observePlayers());
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
        if (entities.isDirty()) {
            heartbeatEntities();
            return;
        }

        if (!isDirty())
            return;

        boolean hadChange = false;
        Entry<?, Boolean> current = this.displayName.get();
        if (current.value())
            hadChange = true;

        Component displayName = (Component) current.key();

        current = this.nameTagVisibilityRule.get();
        if (current.value())
            hadChange = true;

        NameTagVisibilityRule nameTagVisibilityRule = (NameTagVisibilityRule) current.key();

        current = this.collisionRule.get();
        if (current.value())
            hadChange = true;

        CollisionRule collisionRule = (CollisionRule) current.key();

        current = this.prefix.get();
        if (current.value())
            hadChange = true;

        Component prefix = (Component) current.key();

        current = this.suffix.get();
        if (current.value())
            hadChange = true;

        Component suffix = (Component) current.key();

        current = this.formatting.get();
        if (current.value())
            hadChange = true;

        ChatFormatting formatting = (ChatFormatting) current.key();

        current = this.friendlyFire.get();
        if (current.value())
            hadChange = true;

        Byte friendlyFire = (Byte) current.key();

        if (!hadChange)
            return;

        sendUpdateTeamPacket(
                group.players(),
                displayName,
                friendlyFire,
                nameTagVisibilityRule,
                collisionRule,
                formatting,
                prefix,
                suffix
        );
    }

    private void heartbeatEntities() {
        Set<String> cacheEntities = entities.getCache();
        if (cacheEntities == null)
            cacheEntities = Set.of();

        Entry<Set<String>, Boolean> entry = entities.get();
        if (!entry.value())
            return;

        // Compare
        Set<String> toAdd = new HashSet<>(entry.key());
        toAdd.removeAll(cacheEntities);

        Set<String> toRemove = new HashSet<>(cacheEntities);
        toRemove.removeAll(entry.key());

        if (!toAdd.isEmpty())
            sendAddEntitiesPacket(group.players(), toAdd);

        if (!toRemove.isEmpty())
            sendRemoveEntitiesPacket(group.players(), toRemove);
    }

    public BoardPlayerGroup<P> getGroup() {
        return group;
    }

    public DisplayTeam<P> addEntity(String... entity) {
        ((SetObservable<String>) entities.getWatchedObservable()).addAll(Set.of(entity));
        return this;
    }

    public SetObservable<String> getEntities() {
        return (SetObservable<String>) entities.getWatchedObservable();
    }

    public DisplayTeam<P> removeEntity(String... entity) {
        ((SetObservable<String>) entities.getWatchedObservable()).removeAll(Set.of(entity));
        return this;
    }

    public DisplayTeam<P> setDisplayName(Observable<Component> displayName) {
        if (this.displayName != null && this.displayName != displayName)
            this.displayName.delete();

        this.displayName = DirtyWatcher.create(displayName);
        return this;
    }

    public DisplayTeam<P> setNameTagVisibilityRule(Observable<NameTagVisibilityRule> nameTagVisibilityRule) {
        if (this.nameTagVisibilityRule != null && this.nameTagVisibilityRule != nameTagVisibilityRule)
            this.nameTagVisibilityRule.delete();

        this.nameTagVisibilityRule = DirtyWatcher.create(nameTagVisibilityRule);
        return this;
    }

    public DisplayTeam<P> setCollisionRule(Observable<CollisionRule> collisionRule) {
        if (this.collisionRule != null && this.collisionRule != collisionRule)
            this.collisionRule.delete();

        this.collisionRule = DirtyWatcher.create(collisionRule);
        return this;
    }

    public DisplayTeam<P> setPrefix(Observable<Component> prefix) {
        if (this.prefix != null && this.prefix != prefix)
            this.prefix.delete();

        this.prefix = DirtyWatcher.create(prefix);
        return this;
    }

    public DisplayTeam<P> setSuffix(Observable<Component> suffix) {
        if (this.suffix != null && this.suffix != suffix)
            this.suffix.delete();

        this.suffix = DirtyWatcher.create(suffix);
        return this;
    }

    public DisplayTeam<P> setFormatting(Observable<ChatFormatting> formatting) {
        if (this.formatting != null && this.formatting != formatting)
            this.formatting.delete();

        this.formatting = DirtyWatcher.create(formatting);
        return this;
    }

    /**
     * Bit mask. 0x01: Allow friendly fire, 0x02: can see invisible entities on same team.
     * @param friendlyFire friendlyFire
     */
    public DisplayTeam<P> setFriendlyFire(Observable<Byte> friendlyFire) {
        if (this.friendlyFire != null && this.friendlyFire != friendlyFire)
            this.friendlyFire.delete();

        this.friendlyFire = DirtyWatcher.create(friendlyFire);
        return this;
    }

    public void delete() {
        sendRemoveTeamPacket(group.players());

        entities.delete();
        displayName.delete();
        nameTagVisibilityRule.delete();
        collisionRule.delete();
        prefix.delete();
        suffix.delete();
        formatting.delete();
        friendlyFire.delete();
    }

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

    public abstract void sendAddEntitiesPacket(
            Set<P> audience,
            Collection<String> addEntities
    );

    public abstract void sendRemoveEntitiesPacket(
            Set<P> audience,
            Collection<String> removeEntities
    );

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

    public abstract void sendRemoveTeamPacket(Set<P> audience);

}
