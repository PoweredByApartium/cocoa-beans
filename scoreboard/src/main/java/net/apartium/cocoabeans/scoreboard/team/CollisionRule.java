package net.apartium.cocoabeans.scoreboard.team;

public enum CollisionRule {
    ALWAYS("always", 0),
    NEVER("never", 1),
    PUSH_OTHER_TEAMS("pushOtherTeams", 2),
    PUSH_OWN_TEAM("pushOwnTeam", 3);

    private final String name;
    private final int id;

    CollisionRule(String name, int id) {
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
