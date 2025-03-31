package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;

public class MovementSystem extends EntitySystem {

    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private final ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity entity : entities) {
            PositionComponent pos = pm.get(entity);
            VelocityComponent vel = vm.get(entity);

            pos.previousPosition.set(pos.currentPosition);
            pos.currentPosition.x += vel.velocity.x * deltaTime;
            pos.currentPosition.y += vel.velocity.y * deltaTime;

            calculateLookingDirection(entity);
        }
    }

    private void calculateLookingDirection(Entity entity) {
        final float DIRECTION_EPSILON = 0.05f;
        PositionComponent pos = pm.get(entity);

        float deltaX = pos.currentPosition.x - pos.previousPosition.x;
        float deltaY = pos.currentPosition.y - pos.previousPosition.y;

        if (Math.abs(deltaX) > DIRECTION_EPSILON || Math.abs(deltaY) > DIRECTION_EPSILON) {
            pos.lookingDirection.set(deltaX, deltaY).nor();
        } else {
            // optional: nicht verändern oder auf null setzen
            pos.lookingDirection.set(0, 0);
        }

    }

}
