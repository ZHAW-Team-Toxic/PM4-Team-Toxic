package com.zhaw.frontier.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;

public class RenderSystem extends EntitySystem {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<RenderComponent> rm = ComponentMapper.getFor(RenderComponent.class);
    private Engine engine;

    public RenderSystem(SpriteBatch batch, OrthographicCamera camera, Engine engine) {
        this.batch = batch;
        this.camera = camera;
        this.engine = engine;
    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for (Entity entity : engine.getEntities()) {
            PositionComponent pos = pm.get(entity);
            RenderComponent render = rm.get(entity);
            
            if (pos != null && render != null) {
                batch.draw(render.texture, pos.position.x, pos.position.y);
            }
        }
        
        batch.end();
    }
}
