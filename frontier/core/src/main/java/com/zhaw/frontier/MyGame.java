package com.zhaw.frontier;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.systems.BoundsSystem;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.systems.RenderSystem;

public class MyGame extends ApplicationAdapter {
    private Engine engine;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private float time = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);
        engine = new Engine();

        // Add systems
        engine.addSystem(new MovementSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new RenderSystem(batch, camera, engine));

        // Add characters
        engine.addEntity(createCharacter("donkey.png", 100, 100, true));
        engine.addEntity(createCharacter("donkey.png", 300, 300, false));
    }

    private Entity createCharacter(String texturePath, float x, float y, boolean circularMovement) {
        Entity entity = new Entity();

        PositionComponent pos = new PositionComponent();
        pos.position.set(x, y);

        VelocityComponent vel = new VelocityComponent();

        if (circularMovement) {
            vel.velocity.set(50, 0);
        } else {
            vel.velocity.set(100, 0);
        }

        var render = new RenderComponent();
        render.texture = new Texture(Gdx.files.internal(texturePath));

        entity.add(pos).add(vel).add(render);
        return entity;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        time += Gdx.graphics.getDeltaTime();

        engine.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Entity entity : engine.getEntities()) {
            RenderComponent render = ComponentMapper.getFor(RenderComponent.class).get(entity);
            if (render != null && render.texture != null) {
                render.texture.dispose();
            }
        }
    }
}
