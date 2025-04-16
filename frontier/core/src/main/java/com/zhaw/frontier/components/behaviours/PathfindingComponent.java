package com.zhaw.frontier.components.behaviours;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathfindingComponent implements Component {

    public Array<Vector2> waypoints = new Array<>();
    public Vector2 destination = null;
    public boolean pathCompleted = false;
    public boolean needsRepath = false;

    public boolean hasPath() {
        return waypoints.size > 0;
    }

    public Vector2 getNextWaypoint() {
        return hasPath() ? waypoints.first() : null;
    }

    public void advanceToNextWaypoint() {
        if (hasPath()) {
            waypoints.removeIndex(0);
            if (waypoints.size == 0) pathCompleted = true;
        }
    }
}
