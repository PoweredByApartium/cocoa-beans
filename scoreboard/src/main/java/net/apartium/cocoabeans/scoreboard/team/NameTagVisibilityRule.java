package net.apartium.cocoabeans.scoreboard.team;

import org.jetbrains.annotations.ApiStatus;

/**
 * Name tag visibility configuration
 */
@ApiStatus.AvailableSince("0.0.41")
public enum NameTagVisibilityRule {
    ALWAYS("always"),
    NEVER("never"),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam");

    private final String name;

    NameTagVisibilityRule(String name) {
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
