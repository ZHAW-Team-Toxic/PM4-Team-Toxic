package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;

public class MovementSystem extends IteratingSystem {
    public MovementSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = ComponentMapper.getFor(PositionComponent.class).get(entity);
        VelocityComponent velocity = ComponentMapper.getFor(VelocityComponent.class).get(entity);

        position.position.x += velocity.velocity.x * deltaTime;
        position.position.y += velocity.velocity.y * deltaTime;
    }
}
