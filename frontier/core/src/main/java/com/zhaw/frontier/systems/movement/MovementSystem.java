package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.DeathComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;

/**
 * A system that updates the position of all entities with both {@link PositionComponent}
 * and {@link VelocityComponent}, applying simple linear movement.
 * <p>
 * The system multiplies each entity's velocity by {@code deltaTime} and adds it to its position.
 * </p>
 */
public class MovementSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> pfm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );

    private ImmutableArray<Entity> movables;

    public MovementSystem() {
        super();
        Gdx.app.debug("MovementSystem", "initialized");
    }

    /**
     * Called when the system is added to the engine. Collects all entities that have both
     * {@link PositionComponent} and {@link VelocityComponent}.
     *
     * @param engine the {@link Engine} to which this system was added
     */
    @Override
    public void addedToEngine(Engine engine) {
        movables =
        engine.getEntitiesFor(
            Family
                .all(PositionComponent.class, VelocityComponent.class)
                .exclude(DeathComponent.class)
                .get()
        );
    }

    /**
     * Updates the position of each entity based on its velocity and the elapsed time.
     *
     * @param deltaTime the time elapsed since the last frame (in seconds)
     */
    @Override
    public void update(float deltaTime) {
        for (Entity entity : movables) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);

            pos.previousPosition.set(pos.basePosition);
            pos.basePosition.x += vel.velocity.x * deltaTime;
            pos.basePosition.y += vel.velocity.y * deltaTime;

            calculateLookingDirection(entity);
        }
    }

    private void calculateLookingDirection(Entity entity) {
        final float DIRECTION_EPSILON = 0.05f;
        PositionComponent pos = pm.get(entity);
        VelocityComponent vel = vm.get(entity);
        PathfindingBehaviourComponent path = pfm.get(entity);

        // 1. Prefer actual movement (most accurate)
        float dx = pos.basePosition.x - pos.previousPosition.x;
        float dy = pos.basePosition.y - pos.previousPosition.y;

        if (Math.abs(dx) > DIRECTION_EPSILON || Math.abs(dy) > DIRECTION_EPSILON) {
            pos.lookingDirection.set(dx, dy).nor();
            return;
        }

        // 2. Otherwise, fallback to velocity
        if (vel != null && vel.velocity.len2() > DIRECTION_EPSILON * DIRECTION_EPSILON) {
            pos.lookingDirection.set(vel.velocity).nor();
            return;
        }

        // 3. Fallback to intent (next waypoint)
        if (path != null && path.hasPath()) {
            Vector2 dir = new Vector2(path.getNextWaypoint()).sub(pos.basePosition);
            if (dir.len2() > DIRECTION_EPSILON * DIRECTION_EPSILON) {
                pos.lookingDirection.set(dir).nor();
            }
        }
    }
}
