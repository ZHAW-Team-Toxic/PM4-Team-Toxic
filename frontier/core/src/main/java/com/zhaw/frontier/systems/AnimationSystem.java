package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.RenderComponent;

import java.util.Objects;

public class AnimationSystem extends IteratingSystem {

    private final DefaultAnimationManager defaultManager;
    private final QueueAnimationManager conditionalManager;

    public AnimationSystem() {
        super(Family.one(EnemyAnimationComponent.class, BuildingAnimationComponent.class).all(RenderComponent.class).get());
        this.defaultManager = new DefaultAnimationManager();
        this.conditionalManager = new QueueAnimationManager();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        update(entity, deltaTime);
    }

    public void update(Entity entity, float deltaTime){
        AnimationQueueComponent queue = entity.getComponent(AnimationQueueComponent.class);

        if (queue != null && !queue.queue.isEmpty()) {
            conditionalManager.process(entity, deltaTime);
        } else {
            defaultManager.process(entity, deltaTime);
        }
    }
}
