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
import com.zhaw.frontier.components.behaviours.SquarePatrolBehaviourComponent;

public class SquarePatrolBehaviourSystem extends EntitySystem {
    
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private final ComponentMapper<SquarePatrolBehaviourComponent> bm = ComponentMapper.getFor(SquarePatrolBehaviourComponent.class);

    private ImmutableArray<Entity> entities;

     @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(
            Family.all(
                PositionComponent.class,
                VelocityComponent.class,
                SquarePatrolBehaviourComponent.class,
                EnemyComponent.class
            ).exclude(IdleBehaviourComponent.class, PatrolBehaviourComponent.class).get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);
            SquarePatrolBehaviourComponent behavior = bm.get(entity);

            // Initialization
            if (!behavior.initialized) {
                behavior.origin.set(pos.currentPosition.x, pos.currentPosition.y);
                behavior.initialized = true;
            }

            float x = pos.currentPosition.x;
            float y = pos.currentPosition.y;
            float size = behavior.patrolSize;
            float ox = behavior.origin.x;
            float oy = behavior.origin.y;

            switch (behavior.direction) {
                case 0: // Move Right
                    vel.velocity.set(behavior.speed, 0);
                    if (x >= ox + size) {
                        pos.currentPosition.x = ox + size;
                        behavior.direction = 1;
                    }
                    break;
                case 1: // Move Down
                    vel.velocity.set(0, -behavior.speed);
                    if (y <= oy - size) {
                        pos.currentPosition.y = oy - size;
                        behavior.direction = 2;
                    }
                    break;
                case 2: // Move Left
                    vel.velocity.set(-behavior.speed, 0);
                    if (x <= ox) {
                        pos.currentPosition.x = ox;
                        behavior.direction = 3;
                    }
                    break;
                case 3: // Move Up
                    vel.velocity.set(0, behavior.speed);
                    if (y >= oy) {
                        pos.currentPosition.y = oy;
                        behavior.direction = 0;
                    }
                    break;
            }
        }
    }
}
