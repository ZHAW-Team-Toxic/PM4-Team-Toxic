package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.CooldownComponent;

/**
 * CooldownSystem
 */
public class CooldownSystem extends IteratingSystem {
    public CooldownSystem() {
        super(Family.all(CooldownComponent.class).get());
    }

    private ComponentMapper<CooldownComponent> cm = ComponentMapper.getFor(CooldownComponent.class);

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var cooolDownComponent = cm.get(entity);
        if (cooolDownComponent.start + cooolDownComponent.duration <= System.currentTimeMillis()) {
            entity.remove(CooldownComponent.class);
        }
    }

}
