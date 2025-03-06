package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.components.VelocityComponent;
import com.zhaw.frontier.input.RTSInputAdapter;
import com.zhaw.frontier.systems.BoundsSystem;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.ui.GameUi;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

public class GameScreen implements Screen {
    private FrontierGame frontierGame;
    private SpriteBatchWrapper batch;
    private RTSInputAdapter worldInputProcessor;
    // ui
    private Stage stage;
    private GameUi gameUi;

    private ExtendViewport extendedViewport;
    private ScreenViewport screenViewport;
    private Engine engine;

    public GameScreen(FrontierGame game) {
        this.frontierGame = game;
        this.engine = frontierGame.getEngine();
        this.extendedViewport = frontierGame.getExtendedViewport();
        this.screenViewport = frontierGame.getScreenViewport();
        this.batch = frontierGame.getSpriteBatchWrapper();
    }


    @Override
    public void show() {
        var texture = new Texture(Gdx.files.internal("texture-long.png"));
        var textureRegion = new TextureRegion(texture);

        // create rts mouse movements
        worldInputProcessor = new RTSInputAdapter(this.frontierGame.getExtendedViewport());

        // Add systems
        engine.addSystem(new MovementSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new RenderSystem(batch, extendedViewport, engine, textureRegion));

        // Add characters
        engine.addEntity(createCharacter("donkey.png", 1, 1, true));
        engine.addEntity(createCharacter("donkey.png", 3, 3, false));

  
        stage = new Stage(screenViewport, batch.getBatch());
        gameUi = new GameUi(stage);

        // add ui and rts movement to get input events
        var mx = new InputMultiplexer();
        mx.addProcessor(stage);
        mx.addProcessor(worldInputProcessor);
        Gdx.input.setInputProcessor(mx);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        extendedViewport.apply();
        worldInputProcessor.update();
        engine.update(delta);

        if (stage.getBatch().getProjectionMatrix() != null) {
            stage.act();
            stage.draw();
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            frontierGame.switchScreen(new HomeScreen(frontierGame));
        }
    }

    @Override
    public void resize(int width, int height) {
        extendedViewport.update(width, height);
        screenViewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        gameUi.dispose();
        engine.removeAllEntities();
        engine.removeAllSystems();
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

    public Stage getStage() {
        return stage;
    }
}
