package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.RenderComponent;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

/**
 * Initializes all components, systems, ui elements, and viewports needed to
 * render the game.
 * Controls the handling of user input, rendering and gamelogic during each
 * render.
 */
public class GameScreen extends ScreenAdapter {
    private FrontierGame game;
    private SpriteBatchWrapper spriteBatchWrapper;
    private ExtendViewport gameWorldView;
    private ScreenViewport gameUi;
    private Stage stage;
    private Engine engine;

    public GameScreen(FrontierGame frontierGame) {
        this.game = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();

        // create view with world coordinates
        gameWorldView = new ExtendViewport(16, 9);
        gameWorldView.getCamera().position.set(8, 4.5f, 0);

        // setup up ecs(entity component system)
        engine = new Engine();
        engine.addSystem(new RenderSystem(spriteBatchWrapper.getBatch(), gameWorldView, engine));
        engine.addEntity(createEntity());

        // create gameui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        var mx = new InputMultiplexer();
        mx.addProcessor(stage);

        // TODO add mouse input handler mx.addProcessor();
        Gdx.input.setInputProcessor(mx);
    }

    private Entity createEntity() {
        var entity = new Entity();
        var pos = new PositionComponent();
        pos.position = new Vector2(8, 4.5f);
        var render = new RenderComponent();
        render.sprite = new Sprite(game.getAssetManager().get("libgdx.png", Texture.class));
        return entity.add(pos).add(render);
    }

    @Override
    public void render(float delta) {
        frontierGame.getAssetManager().update();

        handleInput();
        engine.update(delta);
        updateUI();
    }

    private void updateUI() {
        gameUi.apply();
        stage.act();
        stage.draw();
    }

    private void handleInput() {
        // TODO handle other input
        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            game.switchScreen(new StartScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        gameUi.update(width, height);
        gameWorldView.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
