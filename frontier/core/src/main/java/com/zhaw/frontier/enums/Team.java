package com.zhaw.frontier.enums;

/**
 * Represents the team or faction an entity belongs to in the game.
 *
 * <p>This enum is commonly used in components like {@link com.zhaw.frontier.components.TeamComponent}
 * to differentiate between allies and enemies for logic such as targeting, combat, or pathfinding.</p>
 */
public enum Team {
    /** The player-controlled team. */
    PLAYER,

    /** The AI-controlled or hostile enemy team. */
    ENEMY,
}
