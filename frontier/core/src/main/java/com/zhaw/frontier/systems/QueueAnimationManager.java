package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.QueueAnimation;
import com.zhaw.frontier.utils.TileOffset;
import java.util.HashMap;

/**
 * Handles conditional (one-shot or time-based) animations for entities.
 * <p>
 * This manager processes the {@link AnimationQueueComponent} of an entity
 * and updates the current animation accordingly, either for enemy entities
 * (with {@link EnemyAnimationComponent}) or buildings (with {@link BuildingAnimationComponent}).
 * </p>
 * <p>
 * When a queued animation ends (based on duration), the system restores the idle state
 * (for enemies) or removes the animation from the active set (for buildings).
 * </p>
 */
public class QueueAnimationManager {

    private final ComponentMapper<EnemyAnimationComponent> enemyAnimM = ComponentMapper.getFor(
        EnemyAnimationComponent.class
    );
    private final ComponentMapper<BuildingAnimationComponent> buildingAnimM =
        ComponentMapper.getFor(BuildingAnimationComponent.class);
    private final ComponentMapper<AnimationQueueComponent> queueM = ComponentMapper.getFor(
        AnimationQueueComponent.class
    );

    /**
     * Processes the animation queue for a given entity.
     * <p>
     * If an animation is currently active (queue not empty), the current animation is applied,
     * updated over time, and rendered. If the animation duration is over and it is non-looping,
     * it is removed and the entity transitions to a default state (idle for enemies).
     * </p>
     *
     * @param entity    the entity to process
     * @param deltaTime the time passed since the last frame
     */
    public void process(Entity entity, float deltaTime) {
        if (!queueM.has(entity)) return;

        AnimationQueueComponent queue = queueM.get(entity);
        if (queue.queue.isEmpty()) return;

        QueueAnimation current = queue.queue.peek();
        current.timeLeft -= deltaTime;

        if (enemyAnimM.has(entity)) {
            EnemyAnimationComponent anim = enemyAnimM.get(entity);
            EnemyAnimationComponent.EnemyAnimationType newAnim =
                (EnemyAnimationComponent.EnemyAnimationType) current.animationType;

            if (anim.currentAnimation != newAnim) {
                anim.currentAnimation = newAnim;
                anim.stateTime = 0f;
                // set frame change
                anim.lastFrameIndex = -1;
            } else {
                anim.stateTime += deltaTime;
            }

            renderEnemySprite(entity);
        }

        if (buildingAnimM.has(entity)) {
            BuildingAnimationComponent anim = buildingAnimM.get(entity);
            BuildingAnimationComponent.BuildingAnimationType type =
                (BuildingAnimationComponent.BuildingAnimationType) current.animationType;

            anim.activeAnimations.add(type);
            anim.stateTimes.merge(type, deltaTime, Float::sum);

            renderBuildingSprite(entity);
        }

        handleFinishedConditionalAnimation(entity, current, queue);
    }

    /**
     * Handles cleanup logic when a conditional animation has finished.
     * <p>
     * Enemies will revert to an idle animation facing their current direction.
     * Buildings will simply remove the animation from the active animation set.
     * </p>
     *
     * @param entity  the entity whose animation finished
     * @param current the finished animation
     * @param queue   the animation queue component
     */
    private void handleFinishedConditionalAnimation(
        Entity entity,
        QueueAnimation current,
        AnimationQueueComponent queue
    ) {
        if (current.timeLeft <= 0 && !current.loop) {
            queue.queue.poll();

            if (enemyAnimM.has(entity)) {
                EnemyAnimationComponent anim = enemyAnimM.get(entity);
                PositionComponent pos = entity.getComponent(PositionComponent.class);

                if (pos != null) {
                    // Reset to correct idle direction
                    // whenever an enemy finishes an animation it goes as default first to an idle animation in the direction it is looking
                    if (pos.lookingDirection.x > 0 && pos.lookingDirection.y == 0) {
                        anim.currentAnimation =
                        EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT;
                    } else if (pos.lookingDirection.x < 0 && pos.lookingDirection.y == 0) {
                        anim.currentAnimation =
                        EnemyAnimationComponent.EnemyAnimationType.IDLE_LEFT;
                    } else if (pos.lookingDirection.x == 0 && pos.lookingDirection.y > 0) {
                        anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_UP;
                    } else {
                        anim.currentAnimation =
                        EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN;
                    }

                    anim.stateTime = 0f;
                    anim.lastFrameIndex = -1;
                    renderEnemySprite(entity);
                }
            }

            if (buildingAnimM.has(entity)) {
                BuildingAnimationComponent anim = buildingAnimM.get(entity);

                // Remove finished animation
                anim.activeAnimations.remove(current.animationType);
                anim.stateTimes.remove(current.animationType);
                renderBuildingSprite(entity);
            }
        }
    }

    /**
     * Updates the sprite of an enemy based on its current animation state and frame.
     *
     * @param entity the enemy entity to render
     */
    private void renderEnemySprite(Entity entity) {
        RenderComponent render = entity.getComponent(RenderComponent.class);
        EnemyAnimationComponent anim = enemyAnimM.get(entity);

        if (anim != null && anim.currentAnimation != null) {
            Animation<TextureRegion> animation = anim.animations.get(anim.currentAnimation);
            if (animation != null) {
                int currentFrameIndex = animation.getKeyFrameIndex(anim.stateTime);

                if (currentFrameIndex != anim.lastFrameIndex) {
                    TextureRegion frame = animation.getKeyFrame(anim.stateTime);

                    render.sprites.forEach((offset, layers) -> {
                        for (LayeredSprite layer : layers) {
                            if (layer.zIndex == 0) {
                                layer.region = frame;
                            }
                        }
                    });
                    anim.lastFrameIndex = currentFrameIndex;
                }
            }
        }
    }

    /**
     * Updates the sprite of a building based on all currently active animations.
     *
     * @param entity the building entity to render
     */
    private void renderBuildingSprite(Entity entity) {
        RenderComponent render = entity.getComponent(RenderComponent.class);
        BuildingAnimationComponent anim = buildingAnimM.get(entity);

        if (anim != null) {
            for (BuildingAnimationComponent.BuildingAnimationType type : anim.activeAnimations) {
                HashMap<TileOffset, Animation<TextureRegion>> animationMap = anim.animations.get(
                    type
                );

                if (animationMap != null) {
                    Animation<TextureRegion> animation = animationMap.get(new TileOffset(0, 0));
                    if (animation != null) {
                        int currentFrameIndex = animation.getKeyFrameIndex(
                            anim.stateTimes.get(type)
                        );
                        TextureRegion frame = animation.getKeyFrame(anim.stateTimes.get(type));

                        render.sprites.forEach((offset, layers) -> {
                            for (LayeredSprite layer : layers) {
                                if (layer.zIndex == 0) {
                                    layer.region = frame;
                                }
                            }
                        });
                        // optional add last frame for buildings
                    }
                }
            }
        }
    }
}
