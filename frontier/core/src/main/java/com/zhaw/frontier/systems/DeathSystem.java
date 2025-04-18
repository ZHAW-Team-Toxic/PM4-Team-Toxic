package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.DeathComponent;

public class DeathSystem extends IteratingSystem {

    private final ComponentMapper<DeathComponent> dm = ComponentMapper.getFor(DeathComponent.class);

    public DeathSystem() {
        super(Family.all(DeathComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DeathComponent death = dm.get(entity);
        death.timeUntilRemoval -= deltaTime;
        if (death.timeUntilRemoval <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}
