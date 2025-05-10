package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.GdxExtension;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.systems.movement.MovementSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GdxExtension.class)
public class MovementSystemTest {

    private Engine engine;
    private MovementSystem system;

    @BeforeEach
    void setup() {
        engine = new Engine();
        system = new MovementSystem();
        engine.addSystem(system);
    }

    private Entity createEntity(Vector2 start, Vector2 velocity) {
        Entity entity = new Entity();
        PositionComponent pos = new PositionComponent(start.x, start.y, 1, 1);
        VelocityComponent vel = new VelocityComponent();
        vel.velocity.set(velocity);
        entity.add(pos);
        entity.add(vel);
        return entity;
    }

    @Test
    void testEntityMovesByVelocity() {
        Entity entity = createEntity(new Vector2(0, 0), new Vector2(1, 0));
        engine.addEntity(entity);

        engine.update(1f);

        PositionComponent pos = entity.getComponent(PositionComponent.class);
        assertEquals(1f, pos.basePosition.x, 0.001);
        assertEquals(0f, pos.basePosition.y, 0.001);
    }

    @Test
    void testLookingDirectionFromMovement() {
        Entity entity = createEntity(new Vector2(0, 0), new Vector2(2, 0));
        engine.addEntity(entity);

        engine.update(1f);

        Vector2 look = entity.getComponent(PositionComponent.class).lookingDirection;
        assertEquals(1f, look.x, 0.001);
        assertEquals(0f, look.y, 0.001);
    }

    @Test
    void testLookingDirectionFallsBackToVelocity() {
        Entity entity = createEntity(new Vector2(0, 0), new Vector2(0, 2));
        PositionComponent pos = entity.getComponent(PositionComponent.class);
        pos.previousPosition.set(pos.basePosition); // No delta, so fallback

        engine.addEntity(entity);
        engine.update(0f);

        Vector2 look = pos.lookingDirection;
        assertEquals(0f, look.x, 0.001);
        assertEquals(1f, look.y, 0.001);
    }

    @Test
    void testLookingDirectionFallsBackToWaypoint() {
        Entity entity = createEntity(new Vector2(0, 0), new Vector2(0, 0));
        PathfindingBehaviourComponent path = new PathfindingBehaviourComponent(1f);
        path.waypoints.add(new Vector2(3, 4));
        entity.add(path);

        engine.addEntity(entity);
        engine.update(0f);

        Vector2 look = entity.getComponent(PositionComponent.class).lookingDirection;
        Vector2 expected = new Vector2(3, 4).sub(0, 0).nor();
        assertEquals(expected.x, look.x, 0.001);
        assertEquals(expected.y, look.y, 0.001);
    }
}
