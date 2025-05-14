package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;
import com.zhaw.frontier.enums.Team;

/**
 * Component that assigns an {@link com.badlogic.ashley.core.Entity} to a specific {@link Team}.
 *
 * <p>This is typically used for identifying friend or foe relationships, allowing systems to
 * perform team-based logic such as targeting, collision filtering, or scoring.</p>
 */
public class TeamComponent implements Component {

    /** The team that this entity belongs to. */
    public Team team;

    /**
     * Constructs a {@code TeamComponent} and assigns the entity to the given team.
     *
     * @param team the team this entity belongs to
     */
    public TeamComponent(Team team) {
        this.team = team;
    }
}
