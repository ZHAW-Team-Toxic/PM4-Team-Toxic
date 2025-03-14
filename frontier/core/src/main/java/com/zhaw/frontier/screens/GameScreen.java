package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.CameraComponent;
import com.zhaw.frontier.systems.CameraControlSystem;
import com.zhaw.frontier.systems.MapRenderSystem;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.wrappers.SpriteBatchWrapper;

import java.nio.file.Path;

/**
 * Initializes all components, systems, ui elements, and viewports needed to
 * render the game.
 * Controls the handling of user input, rendering and gamelogic during each
 * render.
 */
public class GameScreen implements Screen {
    private FrontierGame frontierGame;
    private SpriteBatchWrapper spriteBatchWrapper;
    private ExtendViewport gameWorldView;
    private ScreenViewport gameUi;
    private Stage stage;
    private Engine engine;

    private OrthographicCamera camera;

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();

        // create view with world coordinates
        gameWorldView = new ExtendViewport(16, 9);
        gameWorldView.getCamera().position.set(8, 4.5f, 0);

        // setup up ecs(entity component system)
        engine = new Engine();
        engine.addSystem(new RenderSystem(spriteBatchWrapper.getBatch(), gameWorldView, engine));

        // create gameui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        setUpGameCamera();

        CameraControlSystem cameraControlSystem = new CameraControlSystem();
        engine.addSystem(cameraControlSystem);
        Gdx.input.setInputProcessor(cameraControlSystem);

        MapRenderSystem mapRenderSystem = new MapRenderSystem(Path.of("test"), camera, engine);
        engine.addSystem(mapRenderSystem);

        /*
        var mx = new InputMultiplexer();
        mx.addProcessor(stage);
        //todo KEVIN vll lieber so als mit eigenem system?
        // TODO add mouse input handler mx.addProcessor();
        Gdx.input.setInputProcessor(mx);
        */

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
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
            frontierGame.switchScreen(new StartScreen(frontierGame));
        }
    }

    @Override
    public void resize(int width, int height) {
        gameUi.update(width, height);
        gameWorldView.update(width, height);
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
    }

    private void setUpGameCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Entity cameraEntity = new Entity();
        cameraEntity.add(new CameraComponent(camera));
        engine.addEntity(cameraEntity);
    }

}
