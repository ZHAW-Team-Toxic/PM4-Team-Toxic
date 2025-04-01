package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.*;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.zhaw.frontier.components.AnimationComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import java.util.Objects;

public class AnimationSystem extends EntitySystem {

    private final ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(
        AnimationComponent.class
    );
    private final ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(
        RenderComponent.class
    );

    private ImmutableArray<Entity> entities;

    @Override
    public void addedToEngine(Engine engine) {
        entities =
        engine.getEntitiesFor(Family.all(AnimationComponent.class, RenderComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        Gdx.app.debug(
            "AnimationSystem",
            "Updating animations for " + entities.size() + " entities."
        );
        for (Entity entity : entities) {
            AnimationComponent anim = am.get(entity);
            RenderComponent render = rm.get(entity);

            handleEnemyDirection(entity);

            anim.stateTime += deltaTime;
            Animation<TextureRegion> animation = anim.animations.get(anim.currentAnimation);
            if (animation == null) {
                Gdx.app.error("AnimationSystem", "No animation found for " + anim.currentAnimation + " in entity: " + entity);
                continue;
            }
            render.textureRegion = animation
                .getKeyFrame(anim.stateTime, true);
        }
    }

    private void handleEnemyDirection(Entity entity) {
        AnimationComponent anim = am.get(entity);
        RenderComponent render = rm.get(entity);
        PositionComponent pos = entity.getComponent(PositionComponent.class);

        if (Objects.requireNonNull(render.renderType) == RenderComponent.RenderType.ENEMY) {
            float dx = pos.lookingDirection.x;
            float dy = pos.lookingDirection.y;

            if (Math.abs(dx) > Math.abs(dy)) {
                // Horizontal dominiert
                if (dx > 0) {
                    anim.currentAnimation = AnimationComponent.AnimationType.WALK_RIGHT;
                } else {
                    anim.currentAnimation = AnimationComponent.AnimationType.WALK_LEFT;
                }
            } else if (Math.abs(dy) > 0) {
                // Vertikal dominiert
                if (dy > 0) {
                    anim.currentAnimation = AnimationComponent.AnimationType.WALK_UP;
                } else {
                    anim.currentAnimation = AnimationComponent.AnimationType.WALK_DOWN;
                }
            }
        }
    }
}
