package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

public class RenderSystem extends EntitySystem {
    private SpriteBatch batch;
    private Viewport viewport;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);
    private Engine engine;
    private TextureRegion background;

    public RenderSystem(SpriteBatch batch, Viewport viewport, Engine engine, TextureRegion background) {
        this.batch = batch;
        this.engine = engine;
        this.viewport = viewport;
        this.background = background;
    }

    @Override
    public void update(float deltaTime) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(background, 0, 0, 16, 8);

        for (Entity entity : engine.getEntities()) {
            PositionComponent pos = pm.get(entity);
            RenderComponent render = rm.get(entity);

            if (pos != null && render != null) {
                batch.draw(render.texture, pos.position.x, pos.position.y, 1, 1);
            }
        }

        batch.end();
    }
}
