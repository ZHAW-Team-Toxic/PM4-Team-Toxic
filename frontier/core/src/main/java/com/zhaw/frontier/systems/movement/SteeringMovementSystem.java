package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.configs.AppProperties;

/**
 * A movement system that applies local steering behavior to avoid overlap between pathfinding entities.
 * <p>
 * This system adds an avoidance vector to the desired velocity of each entity,
 * based on the proximity of other pathfinding entities around it.
 * It ensures smooth and realistic motion by preventing units from crowding or overlapping.
 * </p>
 *
 * <p>Requirements for each processed entity:</p>
 * <ul>
 *   <li>{@link PositionComponent} – to determine the current position</li>
 *   <li>{@link VelocityComponent} – to read and write movement directions</li>
 *   <li>{@link PathfindingBehaviourComponent} – for speed configuration</li>
 * </ul>
 */
public class SteeringMovementSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<PathfindingBehaviourComponent> patm = ComponentMapper.getFor(
        PathfindingBehaviourComponent.class
    );

    private ImmutableArray<Entity> enemies;

    /**
     * Called when the system is added to the engine.
     * Filters entities that can move and perform avoidance.
     *
     * @param engine the Ashley engine instance
     */
    @Override
    public void addedToEngine(Engine engine) {
        enemies =
        engine.getEntitiesFor(
            Family
                .all(
                    PositionComponent.class,
                    VelocityComponent.class,
                    PathfindingBehaviourComponent.class
                )
                .get()
        );
    }

    /**
     * Updates all entities with local avoidance steering.
     * <p>
     * Each entity checks its distance to all other pathfinding entities.
     * If they are too close (within {@link AppProperties#AVOID_RADIUS}), a repulsion force is added.
     * This modified velocity is then applied to the entity.
     * </p>
     *
     * @param deltaTime the time since the last update (not used in this system)
     */
    @Override
    public void update(float deltaTime) {
        int size = enemies.size();

        for (int i = 0; i < size; i++) {
            Entity current = enemies.get(i);
            PositionComponent pos = pm.get(current);
            VelocityComponent vel = vm.get(current);

            // Start with the desired velocity set by the pathfinding system
            Vector2 finalVelocity = new Vector2(vel.desiredVelocity);
            Vector2 avoidance = new Vector2();

            for (int j = 0; j < size; j++) {
                if (i == j) continue;

                Entity other = enemies.get(j);
                PositionComponent otherPos = pm.get(other);
                float dist = pos.basePosition.dst(otherPos.basePosition);

                if (dist < AppProperties.AVOID_RADIUS && dist > 0.01f) {
                    Vector2 push = new Vector2(pos.basePosition).sub(otherPos.basePosition);
                    avoidance.add(
                        push
                            .nor()
                            .scl((AppProperties.AVOID_RADIUS - dist) * AppProperties.AVOID_STRENGTH)
                    );
                }
            }

            finalVelocity.add(avoidance);
            float speed = patm.has(current) ? patm.get(current).speed : 2f;
            finalVelocity.limit(speed);

            vel.velocity.set(finalVelocity);
            if (finalVelocity.len2() > 0.001f) {
                pos.lookingDirection.set(finalVelocity).nor();
            }
        }
    }
}
