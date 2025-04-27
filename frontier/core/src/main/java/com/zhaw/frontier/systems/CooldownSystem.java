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

    private final ComponentMapper<CooldownComponent> cm = ComponentMapper.getFor(CooldownComponent.class);

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        var coolDownComponent = cm.get(entity);
        if (coolDownComponent.start + coolDownComponent.duration <= System.currentTimeMillis()) {
            entity.remove(CooldownComponent.class);
        }
    }
}
