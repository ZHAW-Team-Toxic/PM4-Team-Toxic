package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.AnimationComponent;
import com.zhaw.frontier.components.PositionComponent;

/**
 * AnimationSystem
 */
public class AnimationSystem extends EntitySystem {
    private SpriteBatch batch;
    private Viewport viewport;
    private Engine engine;
    private float stateTime;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<AnimationComponent> am = ComponentMapper.getFor(AnimationComponent.class);
    private Family family = Family.all(PositionComponent.class, AnimationComponent.class).get();

    public AnimationSystem(SpriteBatch batch, Viewport viewport, Engine engine) {
        this.batch = batch;
        this.engine = engine;
        this.viewport = viewport;
    }

    @Override
    public void update(float deltaTime) {
        stateTime += deltaTime;
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (Entity entity : engine.getEntitiesFor(family)) {
            PositionComponent pos = pm.get(entity);
            AnimationComponent ac = am.get(entity);

            TextureRegion currentFrame = ac.animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, pos.position.x, pos.position.y, 1, 1);
        }

        batch.end();
    }
}
