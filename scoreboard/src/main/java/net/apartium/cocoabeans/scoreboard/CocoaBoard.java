package net.apartium.cocoabeans.scoreboard;

import net.apartium.cocoabeans.Ensures;
import net.apartium.cocoabeans.state.Observable;
import net.apartium.cocoabeans.state.Observer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;

import java.util.*;

/**
 * CocoaBoard is an easier way to work with scoreboard
 * Support the Observable api that let you make dynamic data change by itself without worrying about it
 * CocoaBoard support multiple server software like (paper, minestom)
 * <br/>
 * CocoaBoard could be for a couple of players or single player also you can change between them
 */
public abstract class CocoaBoard {

    public static final int MAX_LINES = 15;

    protected final String objectiveId;

    private final List<ComponentEntry> lines = new ArrayList<>();
    private final List<ComponentEntry> scores = new ArrayList<>();
    private final List<Style> numberStyles = new ArrayList<>();
    protected final boolean isCustomScoreSupported;

    private ComponentEntry title;

    /**
     * Component Entry
     * wrapper around observable to be able to flag dirty and see Kif need to be updated
     */
    protected static class ComponentEntry implements Observer {

        private final Observable<Component> component;

        private boolean isDirty;
        private Component prevComponent = null;

        private static ComponentEntry create(Observable<Component> component) {
            if (component == null)
                return null;

            return new ComponentEntry(component);
        }

        private ComponentEntry(Observable<Component> component) {
            this.component = component;

            this.component.observe(this);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (obj == null)
                return false;

            if (obj.getClass() != this.getClass())
                return false;

            ComponentEntry other = (ComponentEntry) obj;
            if (this.component != other.component)
                return false;

            return this.isDirty() == other.isDirty();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.component, this.isDirty);
        }

        @Override
        public void flagAsDirty(Observable<?> observable) {
            if (component != observable)
                return;

            isDirty = true;
        }

        public void delete() {
            component.removeObserver(this);
            prevComponent = null;
        }

        /**
         * clean the entry
         */
        public void clean() {
            isDirty = false;
            prevComponent = component.get();
        }

        /**
         * If the entry is dirty or not
         * @return isDirty
         */
        public boolean isDirty() {
            return isDirty;
        }

        public boolean hasChange() {
            if (!isDirty)
                return false;

            return !Objects.equals(prevComponent, component.get());
        }

        /**
         * Return the component
         * @return component
         */
        public Observable<Component> component() {
            return component;
        }
    }

    /**
     * Constructor for CocoaBoard
     * @apiNote If you implement call CocoaBoard#createBoardAndDisplay
     * @param objectiveId scoreboard objective id (Should be unique to support player receiving multiple CocoaBoard)
     * @param title title of the scoreboard
     * @param isCustomScoreSupported whether the version support custom score display or not
     */
    protected CocoaBoard(String objectiveId, Observable<Component> title, boolean isCustomScoreSupported) {
        this.objectiveId = objectiveId;
        this.title = ComponentEntry.create(title);

        this.isCustomScoreSupported = isCustomScoreSupported;
    }

    protected void createBoardAndDisplay() {
        sendObjectivePacket(ObjectiveMode.CREATE, title.component);
        sendDisplayPacket();
    }

    /**
     * Set display to sidebar
     */
    public void setDisplay() {
        sendDisplayPacket();
    }

    private void ensuresLineNumber(int score, boolean ensureInRange, boolean ensuresMax) {
        Ensures.largerThan(score, 0, new IllegalArgumentException("Line must be positive number"));

        if (ensureInRange)
            Ensures.largerThan(lines.size(), score, new IllegalArgumentException("Line must be under " + lines.size()));

        if (ensuresMax)
            Ensures.largerThan(MAX_LINES, score, new IllegalArgumentException("Line must be at most " + MAX_LINES));
    }

    /**
     * Check if the observable had change and if they did update it in the scoreboard
     */
    public void heartbeat() {
        if (title != null && title.hasChange())
            updateTitle();


        for (int score = 0; score < lines.size(); score++) {
            ComponentEntry entry = lines.get(score);

            if (entry == null || !entry.hasChange())
                continue;

            entry.clean();
            sendLineChange(getScoreByLine(score), entry);
        }

        if (isCustomScoreSupported) {
            for (int score = 0; score < scores.size(); score++) {
                ComponentEntry entry = scores.get(score);

                if (entry == null || !entry.hasChange())
                    continue;

                entry.clean();
                sendScorePacket(
                        score,
                        entry.component,
                        ScoreboardAction.CREATE_OR_UPDATE,
                        null
                );
            }
        }
    }

    /**
     * Set a line in the scoreboard with simple component
     * @param line line number (0 is the top)
     * @param component the static content
     */
    public void line(int line, Component component) {
        line(line, toObservable(component));
    }

    /**
     * Set a line in the scoreboard with observable component
     * @param line line number (0 is the top)
     * @param component the observable content
     */
    public void line(int line, Observable<Component> component) {
        line(line, component, null);
    }

    /**
     * Set a line in the scoreboard with simple component & scoreDisplay
     * @param line line number (0 is the top)
     * @param component the static content
     * @param scoreDisplay custom score display - Note: (+1.20.3)
     */
    public void line(int line, Component component, Component scoreDisplay) {
        line(line, toObservable(component), toObservable(scoreDisplay));
    }

    /**
     * Set a line in the scoreboard with simple component & scoreDisplay
     * @param line line number (0 is the top)
     * @param component the observable content
     * @param scoreDisplay custom score display - Note: (+1.20.3)
     */
    public void line(int line, Observable<Component> component, Observable<Component> scoreDisplay) {
        line(line, component, scoreDisplay, null);
    }

    /**
     * Set a line in the scoreboard with simple component & scoreDisplay or custom numberStyle
     * @param line line number (0 is the top)
     * @param component the static content
     * @param scoreDisplay custom score display - Note: (+1.20.3)
     * @param numberStyle the side number style if scoreDisplay is null - Note: (+1.20.3)
     * @apiNote If scoreDisplay & numberStyle are null will be blank instead of number - Note: (+1.20.3)
     */
    public void line(int line, Component component, Component scoreDisplay, Style numberStyle) {
        line(line, toObservable(component), toObservable(scoreDisplay), numberStyle);
    }

    /**
     * Set a line in the scoreboard with simple component & scoreDisplay or custom numberStyle
     * @param line line number (0 is the top)
     * @param component the observable content
     * @param scoreDisplay custom score display - Note: (+1.20.3)
     * @param numberStyle the side number style if scoreDisplay is null - Note: (+1.20.3)
     * @apiNote If scoreDisplay & numberStyle are null will be blank instead of number - Note: (+1.20.3)
     */
    public void line(int line, Observable<Component> component, Observable<Component> scoreDisplay, Style numberStyle) {
        ensuresLineNumber(line, false, false);

        if (line < lines.size()) {
            ComponentEntry entry = ComponentEntry.create(component);

            if (hasChange(lines.get(line), entry)) {
                lines.set(line, entry);
                sendLineChange(getScoreByLine(line));
            }

            if (isCustomScoreSupported) {
                ComponentEntry scoreEntry = ComponentEntry.create(scoreDisplay);
                if (!hasChange(scores.get(line), scoreEntry) || Objects.equals(numberStyles.get(line), numberStyle))
                    return;

                scores.set(line, scoreEntry);
                numberStyles.set(line, numberStyle);

                int score = getScoreByLine(line);
                sendScorePacket(score, scoreDisplay, ScoreboardAction.CREATE_OR_UPDATE, numberStyle);
            }

            return;
        }

        List<ComponentEntry> newLines = new ArrayList<>(this.lines);
        List<ComponentEntry> newScores = new ArrayList<>(this.scores);
        List<Style> newNumberStyles = new ArrayList<>(this.numberStyles);

        if (line > lines.size()) {
            for (int i = lines.size(); i < line; i++) {
                newLines.add(ComponentEntry.create(Observable.empty()));
                newScores.add(null);
                newNumberStyles.add(null);
            }
        }

        newLines.add(ComponentEntry.create(component));
        newScores.add(ComponentEntry.create(scoreDisplay));
        newNumberStyles.add(numberStyle);

        lines0(newLines, newScores, newNumberStyles);
    }


    public void add(Component component) {
        add(toObservable(component));
    }

    public void add(Observable<Component> component) {
        add(component, null);
    }

    public void add(Component component, Component scoreDisplay) {
        add(toObservable(component), toObservable(scoreDisplay));
    }

    public void add(Observable<Component> component, Observable<Component> scoreDisplay) {
        add(component, scoreDisplay, null);
    }

    public void add(Component component, Component scoreDisplay, Style numberStyle) {
        add(toObservable(component), scoreDisplay == null ? null : toObservable(scoreDisplay), numberStyle);
    }

    public void add(Observable<Component> component, Observable<Component> scoreDisplay, Style numberStyle) {
        List<ComponentEntry> newLines = new ArrayList<>(this.lines);
        List<ComponentEntry> newScores = new ArrayList<>(this.scores);
        List<Style> newNumberStyles = new ArrayList<>(this.numberStyles);

        newLines.add(ComponentEntry.create(component));
        newScores.add(ComponentEntry.create(scoreDisplay));
        newNumberStyles.add(numberStyle);

        lines0(newLines, newScores, newNumberStyles);
    }

    public void add(Component component, int offsetLine) {
        add(toObservable(component), offsetLine);
    }

    public void add(Observable<Component> component, int offsetLine) {
        add(component, null, offsetLine);
    }

    public void add(Component component, Component scoreDisplay, int offsetLine) {
        add(toObservable(component), toObservable(scoreDisplay), offsetLine);
    }

    public void add(Observable<Component> component, Observable<Component> scoreDisplay, int offsetLine) {
        add(component, scoreDisplay, null, offsetLine);
    }

    public void add(Component component, Component scoreDisplay, Style numberStyle, int offsetLine) {
        add(toObservable(component), toObservable(scoreDisplay), numberStyle, offsetLine);
    }

    public void add(Observable<Component> component, Observable<Component> scoreDisplay, Style numberStyle, int offsetLine) {
        List<ComponentEntry> newLines = new ArrayList<>(this.lines);
        List<ComponentEntry> newScores = new ArrayList<>(this.scores);
        List<Style> newNumberStyles = new ArrayList<>(this.numberStyles);

        newLines.add(offsetLine, ComponentEntry.create(component));
        newScores.add(offsetLine, ComponentEntry.create(scoreDisplay));
        newNumberStyles.add(offsetLine, numberStyle);

        lines0(newLines, newScores, newNumberStyles);
    }

    public void remove(int line) {
        ensuresLineNumber(line, true, false);

        List<ComponentEntry> newLines = new ArrayList<>(this.lines);
        List<ComponentEntry> newScores = new ArrayList<>(this.scores);
        List<Style> newNumberStyles = new ArrayList<>(this.numberStyles);

        newLines.remove(line);
        newScores.remove(line);
        newNumberStyles.remove(line);

        lines0(newLines, newScores, newNumberStyles);
    }

    public void updateLines(Collection<Component> lines) {
        lines(lines.stream()
                .map(c -> c == Component.empty()
                        ? Observable.<Component>empty()
                        : Observable.immutable(c)).toList()
        );
    }

    public void updateLines(Collection<Component> lines, Collection<Component> scores) {
        lines(
                lines.stream()
                        .map(c -> c == Component.empty()
                                ? Observable.<Component>empty()
                                : Observable.immutable(c)).toList(),
                scores.stream()
                        .map(c -> c == Component.empty()
                                ? Observable.<Component>empty()
                                : Observable.immutable(c)).toList()
        );
    }

    public void updateLines(Collection<Component> lines, Collection<Component> scores, Collection<Style> numberStyles) {
        lines(
                lines.stream()
                        .map(c -> c == Component.empty()
                                ? Observable.<Component>empty()
                                : Observable.immutable(c)).toList(),
                scores.stream()
                        .map(c -> c == Component.empty()
                                ? Observable.<Component>empty()
                                : Observable.immutable(c)).toList(),
                numberStyles
        );
    }

    public void lines(Collection<Observable<Component>> lines) {
        lines(lines, null);
    }

    public void lines(Collection<Observable<Component>> lines, Collection<Observable<Component>> scores) {
        lines(lines, scores, null);
    }

    public void lines(Collection<Observable<Component>> lines, Collection<Observable<Component>> scores, Collection<Style> numberStyles) {
        Ensures.notNull(lines, "Lines must be non-null");
        if (scores != null && lines.size() != scores.size())
            throw new IllegalArgumentException("Scores and lines must be the same size");

        if (numberStyles != null && lines.size() != numberStyles.size())
            throw new IllegalArgumentException("Number styles and line must be the same size");

        lines0(
                lines.stream().map(ComponentEntry::new).toList(),
                scores == null ? null : scores.stream().map(ComponentEntry::new).toList(),
                numberStyles
        );
    }

    public void title(Component component) {
        title(toObservable(component));
    }

    public void title(Observable<Component> component) {
        this.title = ComponentEntry.create(component);

        updateTitle();
    }

    public void numberStyle(int line, Style style) {
        ensuresLineNumber(line, true, true);

        List<ComponentEntry> newLines = new ArrayList<>(this.lines);
        List<ComponentEntry> newScores = new ArrayList<>(this.scores);
        List<Style> newNumberStyles = new ArrayList<>(this.numberStyles);

        newNumberStyles.set(line, style);

        lines0(newLines, newScores, newNumberStyles);
    }

    private boolean hasChange(ComponentEntry a, ComponentEntry b) {
        return !Objects.equals(a, b) || (a != null && a.hasChange());
    }

    private void lines0(Collection<ComponentEntry> lines, Collection<ComponentEntry> scores, Collection<Style> numberStyles) {
        ensuresLineNumber(lines.size(), false, true);

        if (scores != null && scores.size() != lines.size())
            throw new IllegalArgumentException("The size of the score must match the size of the lines");

        if (numberStyles != null && numberStyles.size() != lines.size())
            throw new IllegalArgumentException("The size of the styles");


        List<ComponentEntry> oldLines = new ArrayList<>(this.lines);
        this.lines.clear();
        this.lines.addAll(lines);

        List<ComponentEntry> oldScores = new ArrayList<>(this.scores);
        this.scores.clear();
        this.scores.addAll(scores != null ? scores : Collections.nCopies(lines.size(), null));

        List<Style> oldNumberStyles = new ArrayList<>(this.numberStyles);
        this.numberStyles.clear();
        this.numberStyles.addAll(numberStyles != null ? numberStyles : Collections.nCopies(lines.size(), null));

        int linesSize = this.lines.size();

        int end = linesSize;
        if (oldLines.size() != linesSize)
            end = handleNotSameSize(oldLines, linesSize);

        for (int i = 0; i < end; i++) {
            if (hasChange(getLineByScore(oldLines, i), getLineByScore(i))) {
                getLineByScore(i).clean();
                sendLineChange(i);
            }

            if (hasChange(
                    getLineByScore(oldScores, i),
                    getLineByScore(this.scores, i)
            ) || !Objects.equals(
                    getLineByScore(oldNumberStyles, i),
                    getLineByScore(this.numberStyles, i))
            ) {
                getLineByScore(this.scores, i).clean();
                sendScorePacket(
                        i,
                        Optional.ofNullable(getLineByScore(this.scores, i))
                                .map(ComponentEntry::component)
                                .orElse(null),
                        ScoreboardAction.CREATE_OR_UPDATE,
                        getLineByScore(this.numberStyles, i)
                );
            }
        }

    }

    private int handleNotSameSize(List<ComponentEntry> oldLines, int linesSize) {
        List<ComponentEntry> oldLinesCopy = new ArrayList<>(oldLines);

        if (oldLines.size() > linesSize) {
            for (int i = oldLinesCopy.size(); i > linesSize; i--) {
                int score = i - 1;

                sendTeamPacket(score, TeamMode.REMOVE, null, null);
                sendScorePacket(score, null, ScoreboardAction.REMOVE, null);

                oldLines.remove(0);
            }

            return linesSize;
        }

        for (int i = oldLinesCopy.size(); i < linesSize; i++) {
            sendScorePacket(
                    i,
                    Optional.ofNullable(getLineByScore(this.scores, i))
                            .map(c -> c.component)
                            .orElse(null),
                    ScoreboardAction.CREATE_OR_UPDATE,
                    getLineByScore(this.numberStyles, i)
            );
            sendTeamPacket(i, TeamMode.CREATE, getLineByScore(i).component, null);
        }

        return oldLines.size(); // No more than what we have
    }

    protected int getScoreByLine(int line) {
        return this.lines.size() - line - 1;
    }

    protected ComponentEntry getLineByScore(int score) {
        return getLineByScore(this.lines, score);
    }

    protected  <E> E getLineByScore(List<E> lines, int score) {
        return score < lines.size() ? lines.get(lines.size() - score - 1) : null;
    }

    private void updateTitle() {
        if (title == null)
            return;

        sendObjectivePacket(ObjectiveMode.UPDATE, title.component);
        title.clean();
    }

    public void delete() {
        for (int i = 0; i < lines.size(); i++) {
            sendTeamPacket(i, TeamMode.REMOVE, null, null);
        }

        sendObjectivePacket(ObjectiveMode.REMOVE, null);

        lines.forEach(ComponentEntry::delete);
        lines.clear();

        scores.forEach(ComponentEntry::delete);
        scores.clear();

        numberStyles.clear();
    }

    public static Observable<Component> toObservable(Component component) {
        if (component == null)
            return null;

        if (component == Component.empty())
            return Observable.empty();

        return Observable.immutable(component);
    }

    protected String intoTeamName(int score) {
        return objectiveId + ":" + score;
    }

    protected abstract void sendObjectivePacket(ObjectiveMode mode, Observable<Component> displayName);
    protected abstract void sendDisplayPacket();
    protected abstract void sendScorePacket(int score, Observable<Component> displayName, ScoreboardAction action, Style numberStyle);
    protected abstract void sendTeamPacket(int score, TeamMode mode, Observable<Component> prefix, Observable<Component> suffix);
    protected abstract void sendLineChange(int score, ComponentEntry line);

    protected void sendLineChange(int score) {
        sendLineChange(score, getLineByScore(score));
    }

}
