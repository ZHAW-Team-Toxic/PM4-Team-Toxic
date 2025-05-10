package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.OccupiesTilesComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.TeamComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.enums.Team;
import com.zhaw.frontier.systems.movement.BlockingMovementSystem;
import com.zhaw.frontier.systems.movement.PathFollowerSystem;
import com.zhaw.frontier.systems.movement.SteeringMovementSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PathfindingMovementTest {

    private Engine engine;

    @BeforeEach
    void setup() {
        engine = new Engine();
    }

    private Entity createMovingEntity(Vector2 position, float speed) {
        Entity entity = new Entity();
        PositionComponent pos = new PositionComponent(position.x, position.y, 1, 1);
        VelocityComponent vel = new VelocityComponent();
        PathfindingBehaviourComponent path = new PathfindingBehaviourComponent(speed);
        path.waypoints.add(new Vector2(position.x + 1, position.y));

        entity.add(pos);
        entity.add(vel);
        entity.add(path);
        entity.add(new TeamComponent(Team.ENEMY));
        return entity;
    }

    @Test
    void testPathFollowerMovesEntityTowardWaypoint() {
        PathFollowerSystem followerSystem = new PathFollowerSystem();
        engine.addSystem(followerSystem);

        Entity entity = createMovingEntity(new Vector2(1, 1), 2f);
        engine.addEntity(entity);

        engine.update(0.016f);

        Vector2 desired = entity.getComponent(VelocityComponent.class).desiredVelocity;
        assertFalse(
            desired.isZero(),
            "Entity should have non-zero desired velocity toward waypoint."
        );
    }

    @Test
    void testSteeringAvoidsNearbyEntities() {
        SteeringMovementSystem steeringSystem = new SteeringMovementSystem();
        engine.addSystem(steeringSystem);

        Entity e1 = createMovingEntity(new Vector2(2, 2), 2f);
        Entity e2 = createMovingEntity(new Vector2(2.3f, 2), 2f);
        engine.addEntity(e1);
        engine.addEntity(e2);

        engine.update(0.016f);

        Vector2 v1 = e1.getComponent(VelocityComponent.class).velocity;
        Vector2 v2 = e2.getComponent(VelocityComponent.class).velocity;

        assertNotEquals(0, v1.len2(), "e1 should have adjusted velocity");
        assertNotEquals(0, v2.len2(), "e2 should have adjusted velocity");
        assertNotEquals(v1, v2, "Entities should steer to avoid overlapping");
    }

    @Test
    void testBlockingMovementSystemDetectsBlocker() {
        BlockingMovementSystem blockerSystem = new BlockingMovementSystem();
        engine.addSystem(blockerSystem);

        Entity enemy = createMovingEntity(new Vector2(2, 2), 2f);

        Entity blocker = new Entity();
        blocker.add(new PositionComponent(3, 2, 1, 1));
        blocker.add(new TeamComponent(Team.PLAYER));
        OccupiesTilesComponent occ = new OccupiesTilesComponent();
        occ.occupiedTiles.add(new Vector2(3, 2));
        blocker.add(occ);

        engine.addEntity(enemy);
        engine.addEntity(blocker);

        engine.update(0.016f);

        PathfindingBehaviourComponent path = enemy.getComponent(
            PathfindingBehaviourComponent.class
        );
        assertTrue(path.isBlockedByEntity(), "Enemy path should be blocked by entity.");
        assertEquals(blocker, path.targetEntity, "Blocked entity should be set as target.");
    }
}
