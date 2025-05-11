package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.DeathComponent;
import com.zhaw.frontier.components.HealthComponent;

/**
 * Healthsystem removes enitities that have no health left
 */
public class HealthSystem extends IntervalIteratingSystem {

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(
        HealthComponent.class
    );

    public HealthSystem() {
        super(Family.all(HealthComponent.class).exclude(DeathComponent.class).get(), 0.5f);
        Gdx.app.debug("HealthSystem", "initialized");
    }

    @Override
    protected void processEntity(Entity entity) {
        var entityHealth = hm.get(entity);

        if (entityHealth.currentHealth <= 0) {
            Gdx.app.debug(
                "HealthSystem",
                "removing entity for having below 0 health" + entity.toString()
            );
            entityHealth.isDead = true;
            entity.add(new DeathComponent(1.0f));
        }
    }
}
