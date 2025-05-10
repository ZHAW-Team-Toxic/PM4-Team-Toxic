package com.zhaw.frontier.systems;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.systems.movement.SteeringMovementSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SteeringMovementSystemTest {

    private Engine engine;
    private SteeringMovementSystem system;

    @BeforeEach
    void setup() {
        engine = new Engine();
        system = new SteeringMovementSystem();
        engine.addSystem(system);
    }

    private Entity createEntity(Vector2 position, Vector2 desiredVelocity) {
        Entity entity = new Entity();
        PositionComponent pos = new PositionComponent(position.x, position.y, 1, 1);
        VelocityComponent vel = new VelocityComponent();
        vel.desiredVelocity.set(desiredVelocity);
        PathfindingBehaviourComponent path = new PathfindingBehaviourComponent(2f);

        entity.add(pos);
        entity.add(vel);
        entity.add(path);
        return entity;
    }

    @Test
    void testLookingDirectionAlignsWithVelocity() {
        Vector2 desired = new Vector2(1f, 1f);
        Entity entity = createEntity(new Vector2(0, 0), desired);

        engine.addEntity(entity);
        engine.update(0.016f);

        Vector2 actualDirection = entity.getComponent(PositionComponent.class).lookingDirection;
        Vector2 expectedDirection = new Vector2(desired).nor();

        assertEquals(expectedDirection.x, actualDirection.x, 0.001f);
        assertEquals(expectedDirection.y, actualDirection.y, 0.001f);
    }

    @Test
    void testNoLookingDirectionWhenVelocityIsZero() {
        Entity entity = createEntity(new Vector2(0, 0), new Vector2(0f, 0f));
        engine.addEntity(entity);

        engine.update(0.016f);

        Vector2 look = entity.getComponent(PositionComponent.class).lookingDirection;
        assertEquals(
            0f,
            look.len2(),
            0.001f,
            "Looking direction should remain zero if not moving."
        );
    }
}
