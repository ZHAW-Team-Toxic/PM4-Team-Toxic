package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.EntityTypeComponent;
import com.zhaw.frontier.components.EntityTypeComponent.EntityType;
import com.zhaw.frontier.components.HealthComponent;
import com.zhaw.frontier.screens.LoseScreen;

public class LoseConditionSystem extends EntitySystem {

    private final FrontierGame frontierGame;
    private final Family hqFamily = Family.all(HealthComponent.class, EntityTypeComponent.class).get();

    private Entity hq;
    private boolean hqDestroyed = false;

    public LoseConditionSystem(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
    }

    @Override
    public void update(float deltaTime) {
        if (hqDestroyed) return;

        if (hq == null) {
            for (Entity entity : getEngine().getEntitiesFor(hqFamily)) {
                EntityTypeComponent type = entity.getComponent(EntityTypeComponent.class);
                if (type != null && type.type == EntityType.HQ) {
                    hq = entity;
                    break;
                }
            }
        }

        if (hq != null) {
            HealthComponent health = hq.getComponent(HealthComponent.class);
            if (health != null && health.currentHealth <= 0) {
                hqDestroyed = true;
                frontierGame.setScreen(new LoseScreen(frontierGame));
            }
        }
    }
}

