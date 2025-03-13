
package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.VelocityComponent;

public class BoundsSystem extends IteratingSystem {

    public BoundsSystem() {
        super(Family.all(PositionComponent.class, VelocityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var vel = ComponentMapper.getFor(VelocityComponent.class).get(entity);
        var pos = ComponentMapper.getFor(PositionComponent.class).get(entity);

        if (pos.position.x > 16 || pos.position.x < 0) {
            vel.velocity.x *= -1;
        }

        if (pos.position.y > 8 || pos.position.y < 0) {
            vel.velocity.y *= -1;
        }
    }

}
