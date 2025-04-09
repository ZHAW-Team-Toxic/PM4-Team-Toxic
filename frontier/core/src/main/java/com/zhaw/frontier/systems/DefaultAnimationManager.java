package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.utils.LayeredSprite;
import com.zhaw.frontier.utils.TileOffset;

import java.util.HashMap;
import java.util.Objects;

public class DefaultAnimationManager {

    private final ComponentMapper<EnemyAnimationComponent> enemyAnimM = ComponentMapper.getFor(
        EnemyAnimationComponent.class
    );
    private final ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(
        RenderComponent.class
    );
    private final ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private final ComponentMapper<VelocityComponent> velocityM = ComponentMapper.getFor(
        VelocityComponent.class
    );
    private final ComponentMapper<BuildingAnimationComponent> buildingAnimM =
        ComponentMapper.getFor(BuildingAnimationComponent.class);

    public void process(Entity entity, float deltaTime) {
        if (Objects.nonNull(entity.getComponent(RenderComponent.class))) {
            RenderComponent render = rm.get(entity);
            if (render.renderType == RenderComponent.RenderType.ENEMY) {
                if (Objects.nonNull(entity.getComponent(EnemyAnimationComponent.class))) {
                    processEnemy(entity, deltaTime);
                }
            }
            if (render.renderType == RenderComponent.RenderType.BUILDING) {
                if (Objects.nonNull(entity.getComponent(BuildingAnimationComponent.class))) {
                    processBuilding(entity, deltaTime);
                }
            }
        }
    }

    private void processEnemy(Entity entity, float deltaTime) {
        if (!enemyAnimM.has(entity) || !velocityM.has(entity)) return;

        EnemyAnimationComponent anim = enemyAnimM.get(entity);
        RenderComponent render = entity.getComponent(RenderComponent.class);

        handleEnemyDirection(entity);

        anim.stateTime += deltaTime;

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

    private void handleEnemyDirection(Entity entity) {
        EnemyAnimationComponent anim = enemyAnimM.get(entity);
        PositionComponent pos = pm.get(entity);

        float dx = pos.lookingDirection.x;
        float dy = pos.lookingDirection.y;

        EnemyAnimationComponent.EnemyAnimationType newAnim = anim.currentAnimation;

        if (Math.abs(dx) > Math.abs(dy)) {
            newAnim = (dx > 0)
                ? EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT
                : EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT;
        } else if (Math.abs(dy) > 0) {
            newAnim = (dy > 0)
                ? EnemyAnimationComponent.EnemyAnimationType.WALK_UP
                : EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN;
        }

        if (anim.currentAnimation != newAnim) {
            anim.currentAnimation = newAnim;
            anim.stateTime = 0f;
            anim.lastFrameIndex = -1; // ← Reset bei Animationswechsel
        }
    }

    private void processBuilding(Entity entity, float deltaTime) {
        if (!buildingAnimM.has(entity)) return;

        BuildingAnimationComponent anim = buildingAnimM.get(entity);
        for (var type : anim.activeAnimations) {
            anim.stateTimes.merge(type, deltaTime, Float::sum);
        }

        RenderComponent render = rm.get(entity);

        for (BuildingAnimationComponent.BuildingAnimationType type : anim.activeAnimations) {
            HashMap<TileOffset, Animation<TextureRegion>> animationMap = anim.animations.get(
                type
            );
            if (animationMap != null) {
                Animation<TextureRegion> animation = animationMap.get(new TileOffset(0, 0));
                if (animation != null) {
                    render.sprites.forEach((offset, layers) -> {
                        for (LayeredSprite layer : layers) {
                            if (layer.zIndex == 0) {
                                layer.region =
                                    animation.getKeyFrame(anim.stateTimes.get(type), false);
                            }
                        }
                    });
                }
            }
        }

    }

    //todo render buildings
}
