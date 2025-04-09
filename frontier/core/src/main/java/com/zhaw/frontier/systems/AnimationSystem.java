package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;

import java.util.Objects;

public class AnimationSystem extends IteratingSystem {

    private final DefaultAnimationManager defaultManager;
    private final QueueAnimationManager conditionalManager;

    public AnimationSystem() {
        super(Family.all(EnemyAnimationComponent.class, BuildingAnimationComponent.class).get());
        this.defaultManager = new DefaultAnimationManager();
        this.conditionalManager = new QueueAnimationManager();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        update(entity, deltaTime);
    }

    public void update(Entity entity, float deltaTime){
        if(Objects.nonNull(entity.getComponent(AnimationQueueComponent.class))) {
            if (!entity.getComponent(AnimationQueueComponent.class).queue.isEmpty()) {
                conditionalManager.process(entity, deltaTime);
            }
        }
        defaultManager.process(entity, deltaTime);
    }
}
