package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

public class RenderSystem extends EntitySystem {

    private SpriteBatch batch;
    private Viewport viewport;
    private Engine engine;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(
        PositionComponent.class
    );
    private ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(
        RenderComponent.class
    );
    private Family family = Family
        .all(PositionComponent.class, RenderComponent.class)
        .get();

    public RenderSystem(SpriteBatch batch, Viewport viewport, Engine engine) {
        this.batch = batch;
        this.engine = engine;
        this.viewport = viewport;
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // TODO add tile rendering
        // TODO add animation rendering

        Gdx.gl.glClearColor(0, 0, 1, 1); // (0,0,1,1) = Blue
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (Entity entity : engine.getEntitiesFor(family)) {
            PositionComponent pos = pm.get(entity);
            RenderComponent render = rm.get(entity);

            batch.draw(render.sprite, pos.position.x, pos.position.y, 1, 1);
        }

        batch.end();
    }
}
