package net.apartium.cocoabeans.scoreboard.team;

public enum NameTagVisibilityRule {
    ALWAYS("always", 0),
    NEVER("never", 1),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

    private final String name;

    NameTagVisibilityRule(String name, int id) {
        this.name = name;
    }

    /**
     * Get the serialized value of the rule
     * @return serialized value of th rule
     */
    public String getSerializedName() {
        return this.name;
    }
}
