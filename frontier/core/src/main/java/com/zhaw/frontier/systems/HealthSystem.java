package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.zhaw.frontier.components.HealthComponent;

public class HealthSystem extends IntervalIteratingSystem {

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(
        HealthComponent.class
    );

    public HealthSystem() {
        super(Family.all(HealthComponent.class).get(), 0.5f);
        Gdx.app.debug("HealthSystem", "initialized");
    }

    @Override
    protected void processEntity(Entity entity) {
        var entityHealth = hm.get(entity);

        if (entityHealth.Health <= 0) {
            getEngine().removeEntity(entity);
        }
    }
}
