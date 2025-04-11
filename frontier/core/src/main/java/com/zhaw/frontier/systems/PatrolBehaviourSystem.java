package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;

public class PatrolBehaviourSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<PatrolBehaviourComponent> bm = ComponentMapper.getFor(
        PatrolBehaviourComponent.class
    );

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities =
        engine.getEntitiesFor(
            Family
                .all(
                    PositionComponent.class,
                    VelocityComponent.class,
                    PatrolBehaviourComponent.class,
                    EnemyComponent.class
                )
                .exclude(IdleBehaviourComponent.class)
                .get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);
            PatrolBehaviourComponent behavior = bm.get(entity);

            // Lazy init
            if (!behavior.initialized) {
                float originX = pos.basePosition.x;
                float range = 50f; // Default patrol range
                behavior.leftBound = originX;
                behavior.rightBound = originX + range;
                behavior.initialized = true;
            }

            if (behavior.movingRight && pos.basePosition.x >= behavior.rightBound) {
                behavior.movingRight = false;
            } else if (!behavior.movingRight && pos.basePosition.x <= behavior.leftBound) {
                behavior.movingRight = true;
            }

            vel.velocity.set(behavior.movingRight ? behavior.speed : -behavior.speed, 0);
        }
    }
}
