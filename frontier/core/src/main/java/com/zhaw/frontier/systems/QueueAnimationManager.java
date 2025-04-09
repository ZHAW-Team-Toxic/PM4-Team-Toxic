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

public class QueueAnimationManager {

    private final ComponentMapper<EnemyAnimationComponent> enemyAnimM = ComponentMapper.getFor(EnemyAnimationComponent.class);
    private final ComponentMapper<BuildingAnimationComponent> buildingAnimM = ComponentMapper.getFor(BuildingAnimationComponent.class);
    private final ComponentMapper<AnimationQueueComponent> queueM = ComponentMapper.getFor(AnimationQueueComponent.class);

    public void process(Entity entity, float deltaTime) {
        if (!queueM.has(entity)) return;

        AnimationQueueComponent queue = queueM.get(entity);
        if (queue.queue.isEmpty()) return;

        QueueAnimation current = queue.queue.peek();
        current.timeLeft -= deltaTime;

        if (enemyAnimM.has(entity)) {
            EnemyAnimationComponent anim = enemyAnimM.get(entity);
            EnemyAnimationComponent.EnemyAnimationType newAnim = (EnemyAnimationComponent.EnemyAnimationType) current.animationType;

            if (anim.currentAnimation != newAnim) {
                anim.currentAnimation = newAnim;
                anim.stateTime = 0f;
                anim.lastFrameIndex = -1; // Framewechsel sicherstellen
            } else {
                anim.stateTime += deltaTime;
            }

            renderEnemySprite(entity);
        }

        if (buildingAnimM.has(entity)) {
            BuildingAnimationComponent anim = buildingAnimM.get(entity);
            BuildingAnimationComponent.BuildingAnimationType type = (BuildingAnimationComponent.BuildingAnimationType) current.animationType;

            anim.activeAnimations.add(type);
            anim.stateTimes.merge(type, deltaTime, Float::sum);

            renderBuildingSprite(entity);
        }

        if (current.timeLeft <= 0 && !current.loop) {
            queue.queue.poll();

            if (enemyAnimM.has(entity)) {
                EnemyAnimationComponent anim = enemyAnimM.get(entity);
                PositionComponent pos = entity.getComponent(PositionComponent.class);

                if (pos != null) {
                    // Reset to correct idle direction
                    if (pos.lookingDirection.x > 0 && pos.lookingDirection.y == 0) {
                        anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_RIGHT;
                    } else if (pos.lookingDirection.x < 0 && pos.lookingDirection.y == 0) {
                        anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_LEFT;
                    } else if (pos.lookingDirection.x == 0 && pos.lookingDirection.y > 0) {
                        anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_UP;
                    } else {
                        anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.IDLE_DOWN;
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

    private void renderBuildingSprite(Entity entity) {
        RenderComponent render = entity.getComponent(RenderComponent.class);
        BuildingAnimationComponent anim = buildingAnimM.get(entity);

        if (anim != null) {
            for (BuildingAnimationComponent.BuildingAnimationType type : anim.activeAnimations) {
                HashMap<TileOffset, Animation<TextureRegion>> animationMap = anim.animations.get(type);
                if (animationMap != null) {
                    Animation<TextureRegion> animation = animationMap.get(new TileOffset(0, 0));
                    if (animation != null) {
                        int currentFrameIndex = animation.getKeyFrameIndex(anim.stateTimes.get(type));
                        TextureRegion frame = animation.getKeyFrame(anim.stateTimes.get(type));

                        render.sprites.forEach((offset, layers) -> {
                            for (LayeredSprite layer : layers) {
                                if (layer.zIndex == 0) {
                                    layer.region = frame;
                                }
                            }
                        });

                        // Achtung: Für Gebäude haben wir noch kein lastFrameIndex – optional!
                    }
                }
            }
        }
    }
}
