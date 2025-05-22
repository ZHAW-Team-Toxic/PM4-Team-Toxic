package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.EntityTypeComponent;
import com.zhaw.frontier.components.EntityTypeComponent.EntityType;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.screens.LoseScreen;

/**
 * LoseConditionSystem triggers game over when the HQ entity is destroyed.
 */
public class LoseConditionSystem extends IntervalIteratingSystem {

    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(
        HealthComponent.class
    );
    private final ComponentMapper<EntityTypeComponent> etm = ComponentMapper.getFor(
        EntityTypeComponent.class
    );
    private final FrontierGame frontierGame;

    private boolean hqDestroyed = false;

    public LoseConditionSystem(FrontierGame frontierGame) {
        super(Family.all(HealthComponent.class, EntityTypeComponent.class).get(), 0.5f);
        this.frontierGame = frontierGame;
    }

    @Override
    protected void processEntity(Entity entity) {
        if (hqDestroyed) return;

        var type = etm.get(entity);
        if (type.type != EntityType.HQ) return;

        var health = hm.get(entity);
        if (health.currentHealth <= 0) {
            hqDestroyed = true;
            frontierGame.switchScreen(new LoseScreen(frontierGame));
        }
    }
}
