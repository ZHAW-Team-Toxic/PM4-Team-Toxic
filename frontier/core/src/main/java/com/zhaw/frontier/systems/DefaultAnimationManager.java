package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.*;
import com.zhaw.frontier.utils.LayeredSprite;
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
        processEnemy(entity, deltaTime);
        processBuilding(entity, deltaTime);
    }

    private void processEnemy(Entity entity, float deltaTime) {
        if (!enemyAnimM.has(entity) || !velocityM.has(entity)) return;

        EnemyAnimationComponent anim = enemyAnimM.get(entity);
        RenderComponent render = entity.getComponent(RenderComponent.class);

        handleEnemyDirection(entity);

        anim.stateTime += deltaTime;

        Animation<TextureRegion> animation = anim.animations.get(anim.currentAnimation);
        if (animation != null) {
            render.sprites.forEach((offset, layers) -> {
                for (LayeredSprite layer : layers) {
                    if (layer.zIndex == 0) {
                        layer.region = animation.getKeyFrame(anim.stateTime, true);
                    }
                }
            });
        }
    }

    private void handleEnemyDirection(Entity entity) {
        EnemyAnimationComponent anim = enemyAnimM.get(entity);
        RenderComponent render = rm.get(entity);
        PositionComponent pos = pm.get(entity);

        if (Objects.requireNonNull(render.renderType) == RenderComponent.RenderType.ENEMY) {
            float dx = pos.lookingDirection.x;
            float dy = pos.lookingDirection.y;

            if (Math.abs(dx) > Math.abs(dy)) {
                // Horizontal dominiert
                if (dx > 0) {
                    anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.WALK_RIGHT;
                } else {
                    anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.WALK_LEFT;
                }
            } else if (Math.abs(dy) > 0) {
                // Vertikal dominiert
                if (dy > 0) {
                    anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.WALK_UP;
                } else {
                    anim.currentAnimation = EnemyAnimationComponent.EnemyAnimationType.WALK_DOWN;
                }
            }
        }
    }

    private void processBuilding(Entity entity, float deltaTime) {
        if (!buildingAnimM.has(entity)) return;

        BuildingAnimationComponent anim = buildingAnimM.get(entity);
        for (var type : anim.activeAnimations) {
            anim.stateTimes.merge(type, deltaTime, Float::sum);
        }
    }
}
