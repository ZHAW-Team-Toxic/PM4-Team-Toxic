package com.zhaw.frontier;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.input.RTSInputAdapter;
import com.zhaw.frontier.systems.BoundsSystem;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.ui.GameUi;

public class MyGame extends ApplicationAdapter {
    private Engine engine;
    private SpriteBatch batch;
    private ExtendViewport extendedViewport;
    private RTSInputAdapter worldInputProcessor;
    // ui
    private ScreenViewport screenViewport;
    private Stage stage;
    private GameUi gameUi;

    @Override
    public void create() {
        batch = new SpriteBatch();
        var texture = new Texture(Gdx.files.internal("texture-long.png"));
        var textureRegion = new TextureRegion(texture);

        // create view with world coordinates
        extendedViewport = new ExtendViewport(8, 8);
        extendedViewport.getCamera().position.set(8, 4, 0);
        // create rts mouse movements
        worldInputProcessor = new RTSInputAdapter(extendedViewport);

        engine = new Engine();

        // Add systems
        engine.addSystem(new MovementSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new RenderSystem(batch, extendedViewport, engine, textureRegion));

        // Add characters
        engine.addEntity(createCharacter("donkey.png", 1, 1, true));
        engine.addEntity(createCharacter("donkey.png", 3, 3, false));

        // create ui
        screenViewport = new ScreenViewport();
        stage = new Stage(screenViewport, batch);
        gameUi = new GameUi(stage);

        // add ui and rts movement to get input events
        var mx = new InputMultiplexer();
        mx.addProcessor(stage);
        mx.addProcessor(worldInputProcessor);
        Gdx.input.setInputProcessor(mx);
    }

    private Entity createCharacter(String texturePath, float x, float y, boolean circularMovement) {
        Entity entity = new Entity();

        PositionComponent pos = new PositionComponent();
        pos.position.set(x, y);

        VelocityComponent vel = new VelocityComponent();

        if (circularMovement) {
            vel.velocity.set(0.5f, 0);
        } else {
            vel.velocity.set(1, 0);
        }

        var render = new RenderComponent();
        render.texture = new Texture(Gdx.files.internal(texturePath));

        entity.add(pos).add(vel).add(render);
        return entity;
    }

    @Override
    public void resize(int width, int height) {
        extendedViewport.update(width, height);
        screenViewport.update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        extendedViewport.apply();
        worldInputProcessor.update();
        engine.update(Gdx.graphics.getDeltaTime());

        screenViewport.apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        stage.dispose();
        gameUi.dispose();
        for (Entity entity : engine.getEntities()) {
            RenderComponent render = ComponentMapper.getFor(RenderComponent.class).get(entity);
            if (render != null && render.texture != null) {
                render.texture.dispose();
            }
        }
    }
}
