package net.apartium.cocoabeans.tab;

import net.apartium.cocoabeans.scoreboard.ViewerGroup;
import net.apartium.cocoabeans.state.LazyWatcher;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.structs.Entry;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a dynamic tab list header and footer system for a group of players.
 * <p>
 * The header and footer components are driven by an {@link Observable} API,
 * which allows them to be automatically updated via a lazy-watching mechanism.
 * The {@link #heartbeat()} method should be called periodically (e.g., each tick or at intervals)
 * to apply updates if the underlying data has changed.
 * @param <P> type of player
 */
@ApiStatus.AvailableSince("0.0.41")
public abstract class TabList<P> {

    protected final ViewerGroup<P> group;
    protected final LazyWatcher<Set<P>> groupWatcher;

    private final LazyWatcher<Component> header = Observable.<Component>empty().lazyWatch();
    private final LazyWatcher<Component> footer = Observable.<Component>empty().lazyWatch();


    /**
     * Constructs a new {@code TabList} for the specified player group.
     *
     * @param group the group of players this tab list applies to
     */
    protected TabList(ViewerGroup<P> group) {
        this.group = group;
        this.groupWatcher = group.observePlayers().lazyWatch();
    }


    /**
     * Returns the currently viewed players.
     * <p>
     * This reads the last known value of the group watcher and returns an empty set if no value is present.
     *
     * @return the current set of viewers
     */
    protected Set<P> currentViewers() {
        return Optional.ofNullable(groupWatcher.getLastValue())
                .orElse(Set.of());
    }

    /**
     * Checks whether either the header or footer components have changed and requires updating.
     *
     * @return {@code true} if the header or footer is dirty (i.e., changed); {@code false} otherwise
     */
    protected boolean isDirty() {
        return header.isDirty() || footer.isDirty();
    }

    /**
     * Triggers a heartbeat to update the tab list header and footer.
     * <p>
     * This should be called periodically to ensure any updated observable values are applied
     * And will also handle adding & removing viewers.
     */
     public void heartbeat() {
        if (isDirty()) {
            Entry<Component, Boolean> newHeader = header.getOrUpdate();
            Entry<Component, Boolean> newFooter = footer.getOrUpdate();

            if (newHeader.value() || newFooter.value())
                sendPlayerListHeaderAndFooter(
                        currentViewers(),
                        newHeader.key(),
                        newFooter.key()
                );
        }

        heartbeatViewers();
    }

    private void heartbeatViewers() {
        if (!groupWatcher.isDirty())
            return;

        Set<P> cache = Optional.ofNullable(groupWatcher.getLastValue())
                .orElse(Set.of());

        Entry<Set<P>, Boolean> entry = groupWatcher.getOrUpdate();

        if (Boolean.FALSE.equals(entry.value()))
            return;

        Set<P> toAdd = new HashSet<>(entry.key());
        toAdd.removeAll(cache);

        Set<P> toRemove = new HashSet<>(cache);
        toRemove.removeAll(entry.key());

        sendPlayerListHeaderAndFooter(toAdd, header.getLastValue(), footer.getLastValue());
        sendPlayerListHeaderAndFooter(toRemove, null, null);
    }

    /**
     * Returns the associated player group.
     *
     * @return the group of players for this tab list
     */
    public ViewerGroup<P> getGroup() {
        return group;
    }

    /**
     * Sets a static header and footer component.
     *
     * @param header the static header to use
     * @param footer the static footer to use
     */
    public void set(Component header, Component footer) {
        header(header);
        footer(footer);
    }

    /**
     * Sets observable sources for the header and footer.
     * <p>
     * These sources can be dynamic and the tab list will update automatically
     * when the observed values change.
     *
     * @param header an observable header
     * @param footer an observable footer
     */
    public void set(Observable<Component> header, Observable<Component> footer) {
        header(header);
        footer(footer);
    }


    /**
     * Sets a static header component.
     *
     * @param header the static header to display
     */
    public void header(Component header) {
        header(Observable.immutable(header));
    }

    /**
     * Sets an observable header component.
     *
     * @param header an observable header
     */
    public void header(Observable<Component> header) {
        this.header.setDependsOn(header);
    }

    /**
     * Sets a static footer component.
     *
     * @param footer the static footer to display
     */
    public void footer(Component footer) {
        footer(Observable.immutable(footer));
    }

    /**
     * Sets an observable footer component.
     *
     * @param footer an observable footer
     */
    public void footer(Observable<Component> footer) {
        this.footer.setDependsOn(footer);
    }


    /**
     * Clears the tab list header and footer for all current viewers and deletes
     * the associated observable watchers.
     */
    public void delete() {
        sendPlayerListHeaderAndFooter(currentViewers(), null, null);

        this.header.delete();
        this.footer.delete();
    }

    /**
     * Sends the given header and footer components to the specified players.
     *
     * @param viewers the players to send the header/footer to
     * @param header the header component, or {@code null} to clear it
     * @param footer the footer component, or {@code null} to clear it
     */
    protected abstract void sendPlayerListHeaderAndFooter(Set<P> viewers, Component header, Component footer);

}
