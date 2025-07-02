package net.apartium.cocoabeans.scoreboard.team;

import org.jetbrains.annotations.ApiStatus;

/**
 * Collision configuration
 */
@ApiStatus.AvailableSince("0.0.41")
public enum CollisionRule {
    ALWAYS("always"),
    NEVER("never"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam");

    private final String name;

    CollisionRule(String name) {
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
