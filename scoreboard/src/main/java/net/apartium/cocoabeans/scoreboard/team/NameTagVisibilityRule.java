package net.apartium.cocoabeans.scoreboard.team;

public enum NameTagVisibilityRule {
    ALWAYS("always", 0),
    NEVER("never", 1),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

    private final String name;
    private final int id;

    NameTagVisibilityRule(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getSerializedName() {
        return this.name;
    }
}
