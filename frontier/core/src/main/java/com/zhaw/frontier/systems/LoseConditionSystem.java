package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.EntityTypeComponent;
import com.zhaw.frontier.components.EntityTypeComponent.EntityType;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.screens.LoseScreen;

public class LoseConditionSystem extends IteratingSystem {

    private final FrontierGame frontierGame;
    private boolean hqDestroyed = false;

    public LoseConditionSystem(FrontierGame frontierGame) {
        super(Family.all(HealthComponent.class, EntityTypeComponent.class).get());
        this.frontierGame = frontierGame;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (hqDestroyed) return;

        EntityTypeComponent type = entity.getComponent(EntityTypeComponent.class);
        if (type.type != EntityType.HQ) return;

        HealthComponent health = entity.getComponent(HealthComponent.class);
        if (health.currentHealth <= 0) {
            hqDestroyed = true;

            frontierGame.setScreen(new LoseScreen(frontierGame));
        }
    }
}
