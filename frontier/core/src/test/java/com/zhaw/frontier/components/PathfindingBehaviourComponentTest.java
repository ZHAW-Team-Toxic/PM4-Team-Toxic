package com.zhaw.frontier.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import org.junit.jupiter.api.Test;

public class PathfindingBehaviourComponentTest {

    @Test
    void testAdvanceToNextWaypoint() {
        PathfindingBehaviourComponent path = new PathfindingBehaviourComponent(2f);
        path.waypoints.add(new Vector2(1, 1));
        path.waypoints.add(new Vector2(2, 2));

        assertEquals(2, path.waypoints.size);
        assertFalse(path.pathCompleted);

        path.advanceToNextWaypoint();

        assertEquals(1, path.waypoints.size);
        assertFalse(path.pathCompleted);

        path.advanceToNextWaypoint();

        assertEquals(0, path.waypoints.size);
        assertTrue(path.pathCompleted);
    }
}
