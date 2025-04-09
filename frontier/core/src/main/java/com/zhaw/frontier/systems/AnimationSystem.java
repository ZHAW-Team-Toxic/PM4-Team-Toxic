package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.BuildingAnimationComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.RenderComponent;

/**
 * Handles the animation processing of entities based on their animation state.
 *
 * <p>
 * The {@code AnimationSystem} is an Ashley ECS system that manages both conditional (queued)
 * and default animations for entities with either {@link EnemyAnimationComponent} or
 * {@link BuildingAnimationComponent}, and a {@link RenderComponent}.
 * </p>
 *
 * <p>
 * Conditional animations (e.g. attack, damage) take precedence and are handled by
 * {@link QueueAnimationManager}, whereas fallback animations (e.g. idle, walk) are handled
 * by {@link DefaultAnimationManager}.
 * </p>
 */
public class AnimationSystem extends IteratingSystem {

    private final DefaultAnimationManager defaultManager;
    private final QueueAnimationManager conditionalManager;

    /**
     * Creates a new {@code AnimationSystem} that processes entities with animation and rendering components.
     */
    public AnimationSystem() {
        super(
            Family
                .one(EnemyAnimationComponent.class, BuildingAnimationComponent.class)
                .all(RenderComponent.class)
                .get()
        );
        this.defaultManager = new DefaultAnimationManager();
        this.conditionalManager = new QueueAnimationManager();
    }

    /**
     * Called once per frame for each matching entity. Delegates to {@link #update(Entity, float)}.
     *
     * @param entity    the entity to process
     * @param deltaTime time elapsed since last frame (in seconds)
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        update(entity, deltaTime);
    }

    /**
     * Applies animation logic depending on the presence and content of the {@link AnimationQueueComponent}.
     *
     * <p>
     * If the queue is not empty, processes the current conditional animation.
     * Otherwise, applies standard animation updates.
     * </p>
     *
     * @param entity    the entity to update
     * @param deltaTime the time delta since the last frame
     */
    public void update(Entity entity, float deltaTime) {
        AnimationQueueComponent queue = entity.getComponent(AnimationQueueComponent.class);

        if (queue != null && !queue.queue.isEmpty()) {
            conditionalManager.process(entity, deltaTime);
        } else {
            defaultManager.process(entity, deltaTime);
        }
    }
}
