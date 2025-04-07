package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.ConditionalAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;

public class ConditionalAnimationManager {

    private final ComponentMapper<EnemyAnimationComponent> enemyAnimM = ComponentMapper.getFor(
        EnemyAnimationComponent.class
    );
    private final ComponentMapper<BuildingAnimationComponent> buildingAnimM =
        ComponentMapper.getFor(BuildingAnimationComponent.class);
    private final ComponentMapper<AnimationQueueComponent> queueM = ComponentMapper.getFor(
        AnimationQueueComponent.class
    );

    public void process(Entity entity, float deltaTime) {
        if (!queueM.has(entity)) return;

        AnimationQueueComponent queue = queueM.get(entity);
        if (queue.queue.isEmpty()) return;

        ConditionalAnimationComponent current = queue.queue.peek();
        current.timeLeft -= deltaTime;

        if (enemyAnimM.has(entity)) {
            EnemyAnimationComponent anim = enemyAnimM.get(entity);
            anim.currentAnimation =
            (EnemyAnimationComponent.EnemyAnimationType) current.animationType;
            anim.stateTime += deltaTime;
        }

        if (buildingAnimM.has(entity)) {
            BuildingAnimationComponent anim = buildingAnimM.get(entity);
            BuildingAnimationComponent.BuildingAnimationType type =
                (BuildingAnimationComponent.BuildingAnimationType) current.animationType;

            anim.activeAnimations.add(type);
            anim.stateTimes.merge(type, deltaTime, Float::sum);
        }

        if (current.timeLeft <= 0 && !current.loop) {
            queue.queue.poll();

            if (enemyAnimM.has(entity)) {
                EnemyAnimationComponent anim = enemyAnimM.get(entity);
                anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE;
                anim.stateTime = 0f;
            }

            if (buildingAnimM.has(entity)) {
                BuildingAnimationComponent anim = buildingAnimM.get(entity);
                anim.activeAnimations.remove(current.animationType);
                anim.stateTimes.remove(current.animationType);
            }
        }
    }
}
