package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.ConditionalAnimationComponent;

public class AnimationSystem extends IteratingSystem {

    private final DefaultAnimationManager defaultManager;
    private final ConditionalAnimationManager conditionalManager;

    public AnimationSystem() {
        super(Family.all().get()); // Let managers decide which components matter
        this.defaultManager = new DefaultAnimationManager();
        this.conditionalManager = new ConditionalAnimationManager();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.getComponent(ConditionalAnimationComponent.class) != null) {
            conditionalManager.process(entity, deltaTime);
        } else {
            defaultManager.process(entity, deltaTime);
        }
    }
}
