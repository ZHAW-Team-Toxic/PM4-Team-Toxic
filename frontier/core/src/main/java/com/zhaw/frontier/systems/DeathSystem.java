package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.systems.IteratingSystem;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.components.BuildingAnimationComponent.BuildingAnimationType;
import com.zhaw.frontier.components.EnemyAnimationComponent.EnemyAnimationType;
import com.zhaw.frontier.utils.QueueAnimation;

/**
 * A system that handles death logic for entities marked with a {@link DeathComponent}.
 * <p>
 * When an entity dies, this system:
 * <ul>
 *   <li>Plays the appropriate death animation (enemy or building)</li>
 *   <li>Counts down a removal timer</li>
 *   <li>Removes the entity from the engine once the timer expires</li>
 * </ul>
 *
 * <p>Death animations are pushed to the {@link AnimationQueueComponent} and must
 * be configured separately via {@link EnemyAnimationComponent} or {@link BuildingAnimationComponent}.</p>
 */
public class DeathSystem extends IteratingSystem {

    private final ComponentMapper<DeathComponent> dm = ComponentMapper.getFor(DeathComponent.class);
    private final ComponentMapper<AnimationQueueComponent> aqm = ComponentMapper.getFor(
        AnimationQueueComponent.class
    );
    private final ComponentMapper<EnemyAnimationComponent> enemyAnimM = ComponentMapper.getFor(
        EnemyAnimationComponent.class
    );
    private final ComponentMapper<BuildingAnimationComponent> buildingAnimM =
        ComponentMapper.getFor(BuildingAnimationComponent.class);

    /**
     * Constructs the {@code DeathSystem} to process all entities with {@link DeathComponent}.
     */
    public DeathSystem() {
        super(Family.all(DeathComponent.class).get());
    }

    /**
     * Processes a single entity each frame.
     * If the death animation hasn't played, it triggers it.
     * Then it decrements the removal timer and removes the entity once the timer reaches zero.
     *
     * @param entity    the entity to process
     * @param deltaTime time since last frame in seconds
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        DeathComponent death = dm.get(entity);

        if (!death.animationPlayed) {
            playDeathAnimation(entity);
            death.animationPlayed = true;
        }

        death.timeUntilRemoval -= deltaTime;
        if (death.timeUntilRemoval <= 0) {
            getEngine().removeEntity(entity);
        }
    }

    /**
     * Plays the appropriate death animation based on entity type.
     * Pushes the animation to the {@link AnimationQueueComponent}.
     *
     * @param entity the entity to animate
     */
    private void playDeathAnimation(Entity entity) {
        if (!aqm.has(entity)) return;

        QueueAnimation deathAnim = new QueueAnimation();
        deathAnim.loop = false;
        deathAnim.timeLeft = 1.0f;

        if (enemyAnimM.has(entity)) {
            deathAnim.animationType = EnemyAnimationType.DEATH;
        } else if (buildingAnimM.has(entity)) {
            deathAnim.animationType = BuildingAnimationType.DESTROYING;
        }

        AnimationQueueComponent queue = aqm.get(entity);
        queue.queue.clear();
        queue.queue.add(deathAnim);
    }
}
