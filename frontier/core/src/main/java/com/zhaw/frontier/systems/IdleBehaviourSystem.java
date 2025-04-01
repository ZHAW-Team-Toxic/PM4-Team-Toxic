package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.zhaw.frontier.components.EnemyComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.components.behaviours.IdleBehaviourComponent;
import com.zhaw.frontier.components.behaviours.PatrolBehaviourComponent;
import com.zhaw.frontier.components.behaviours.SquarePatrolBehaviourComponent;

public class IdleBehaviourSystem extends EntitySystem {

    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(
        VelocityComponent.class
    );

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities =
        engine.getEntitiesFor(
            Family
                .all(VelocityComponent.class, IdleBehaviourComponent.class, EnemyComponent.class)
                .exclude(PatrolBehaviourComponent.class, SquarePatrolBehaviourComponent.class)
                .get()
        );
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            vm.get(entity).velocity.setZero();
        }
    }
}
