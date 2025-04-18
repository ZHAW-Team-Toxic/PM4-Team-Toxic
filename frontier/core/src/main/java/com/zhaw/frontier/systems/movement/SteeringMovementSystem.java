package com.zhaw.frontier.systems.movement;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.PathfindingBehaviourComponent;
import com.zhaw.frontier.configs.AppProperties;

public class SteeringMovementSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );

    private ImmutableArray<Entity> enemies;

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

    @Override
    public void update(float deltaTime) {
        int size = enemies.size();

        for (int i = 0; i < size; i++) {
            Entity current = enemies.get(i);
            PositionComponent pos = pm.get(current);
            VelocityComponent vel = vm.get(current);

            Vector2 finalVelocity = new Vector2(vel.desiredVelocity); // base intent
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
            finalVelocity.limit(2f); // Optional: cap speed

            vel.velocity.set(finalVelocity);
            if (finalVelocity.len2() > 0.001f) {
                pos.lookingDirection.set(finalVelocity).nor();
            }
        }
    }
}
