package com.zhaw.frontier.components.behaviours;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * A component that stores pathfinding behavior data for an entity.
 * Includes the current path, movement speed, target entity (if any),
 * and flags for whether the path is complete or needs recalculation.
 */
public class PathfindingBehaviourComponent implements Component {

    /** The list of waypoints (positions) the entity should follow. */
    public Array<Vector2> waypoints = new Array<>();

    /** The final destination point of the current path. */
    public Vector2 destination = null;

    /** Whether the entity has finished its current path. */
    public boolean pathCompleted = false;

    /** Whether the path should be recalculated. */
    public boolean needsRepath = false;

    /** The movement speed of the entity. */
    public float speed = 0f;

    /** The target entity this entity is trying to reach, if any. */
    public Entity targetEntity = null;

    /**
     * Constructs a new {@code PathfindingBehaviourComponent} with a given speed.
     *
     * @param speed the movement speed of the entity
     */
    public PathfindingBehaviourComponent(float speed) {
        this.speed = speed;
    }

    /**
     * Checks if the entity currently has a valid path to follow.
     *
     * @return true if the waypoints list is not empty; false otherwise
     */
    public boolean hasPath() {
        return waypoints.size > 0;
    }

    /**
     * Retrieves the next waypoint in the path.
     *
     * @return the next waypoint as a {@link Vector2}, or null if no path exists
     */
    public Vector2 getNextWaypoint() {
        return hasPath() ? waypoints.first() : null;
    }

    /**
     * Advances to the next waypoint in the path.
     * Removes the current waypoint and sets {@code pathCompleted} to true if none remain.
     */
    public void advanceToNextWaypoint() {
        if (hasPath()) {
            waypoints.removeIndex(0);
            if (waypoints.size == 0) pathCompleted = true;
        }
    }

    /**
     * Resets the current path and target entity.
     * Flags the component to request a new path.
     */
    public void resetPath() {
        this.destination = null;
        this.targetEntity = null;
        this.needsRepath = true;
        this.pathCompleted = false;
    }

    /**
     * Assigns a target entity that this entity is trying to follow or reach.
     *
     * @param e the target {@link Entity}
     */
    public void setTargetEntity(Entity e) {
        this.targetEntity = e;
    }

    /**
     * Clears the currently assigned target entity.
     */
    public void clearTargetEntity() {
        this.targetEntity = null;
    }

    /**
     * Determines if the path is currently blocked by a target entity.
     * This is often used to indicate the target is unreachable or has moved.
     *
     * @return true if a target entity is assigned; false otherwise
     */
    public boolean isBlockedByEntity() {
        return targetEntity != null;
    }
}
