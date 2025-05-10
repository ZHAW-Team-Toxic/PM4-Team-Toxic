package com.zhaw.frontier.components;

import com.badlogic.ashley.core.Component;

/**
 * Component that marks an {@link com.badlogic.ashley.core.Entity} for death and removal from the game.
 *
 * <p>It is typically added when an entity's health reaches zero, triggering death-related logic.</p>
 *
 * <p>Includes:</p>
 * <ul>
 *   <li>Time until the entity should be removed from the engine</li>
 *   <li>Whether the death animation has been played</li>
 * </ul>
 */
public class DeathComponent implements Component {

    /** Time in seconds until the entity is removed from the engine. */
    public float timeUntilRemoval;

    /** Whether the death animation has already been played. */
    public boolean animationPlayed = false;

    /**
     * Constructs a {@code DeathComponent} with a specified removal delay.
     *
     * @param timeUntilRemoval the time in seconds until the entity is removed
     */
    public DeathComponent(float timeUntilRemoval) {
        this.timeUntilRemoval = timeUntilRemoval;
    }
}
