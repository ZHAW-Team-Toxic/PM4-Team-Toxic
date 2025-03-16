package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.entities.Tower;
import com.zhaw.frontier.subsystems.BuildingManagerSystem;
import com.zhaw.frontier.subsystems.MapLoaderSystem;
import com.zhaw.frontier.systems.CameraControlSystem;
import com.zhaw.frontier.systems.MapGridSystem;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import java.nio.file.Path;

/**
 * Initializes all components, systems, ui elements, and viewports needed to
 * render the game.
 * Controls the handling of user input, rendering and game logic during each
 * render.
 */
public class GameScreen implements Screen {

    private FrontierGame frontierGame;
    private SpriteBatchInterface spriteBatchWrapper;
    private ExtendViewport gameWorldView;
    private ScreenViewport gameUi;
    private Stage stage;
    private Engine engine;

    private BuildingManagerSystem buildingManagerSystem;

    private OrthogonalTiledMapRenderer renderer;

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        this.renderer = new OrthogonalTiledMapRenderer(null);

        // create view with world coordinates
        gameWorldView = new ExtendViewport(16, 9);
        gameWorldView.getCamera().position.set(2, 8, 0);
        gameWorldView.getCamera().update();

        // setup up ecs(entity component system)
        engine = new Engine();

        //setup map loader system
        MapLoaderSystem mapLoaderSystem;
        try {
            mapLoaderSystem = new MapLoaderSystem(Path.of("frontier_tiled_demo_test.tmx"), engine);

            //setup building manager system
            buildingManagerSystem = new BuildingManagerSystem(mapLoaderSystem.getMapEntity(), gameWorldView);

            //setup render system
            engine.addSystem(new RenderSystem(spriteBatchWrapper.getBatch(), gameWorldView, engine, renderer, mapLoaderSystem, buildingManagerSystem));

        } catch (Exception e) {
            Gdx.app.error("[ERROR] - GameScreen", "Error loading map");
            //TODO try something else?
        }

        // setup camera
        CameraControlSystem cameraControlSystem = new CameraControlSystem(gameWorldView, engine, renderer);
        engine.addSystem(cameraControlSystem);

        //setup grid
        MapGridSystem mapGridSystem = new MapGridSystem(64, 64, 16, 16, gameWorldView.getCamera());
        engine.addSystem(mapGridSystem);

        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        var mx = new InputMultiplexer();
        mx.addProcessor(cameraControlSystem.getInputAdapter());
        mx.addProcessor(stage);
        //TODO add mouse input handler mx.addProcessor();
        Gdx.input.setInputProcessor(mx);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        handleInput();
        engine.update(delta);
        updateUI();
    }

    @Override
    public void resize(int width, int height) {
        gameUi.update(width, height);
        gameWorldView.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
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
        if (Gdx.input.isKeyJustPressed(Keys.B)) {
            //TODO only temporary - will be handled by UI later
            Gdx.app.debug("[DEBUG] - GameScreen", "B pressed");
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Gdx.app.debug("[DEBUG] - GameScreen", "MouseX: " + mouseX + " MouseY: " + mouseY);

            try {
                Tower tower = Tower.createDefaultTower();
                if(buildingManagerSystem.placeBuilding(mouseX, mouseY, tower)){
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building placed");
                } else {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building not placed");
                }
            } catch (Exception e) {
               Gdx.app.error("[ERROR] - GameScreen", "Error placing building");
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.R)) {
            Gdx.app.debug("[DEBUG] - GameScreen", "R pressed");
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Gdx.app.debug("[DEBUG] - GameScreen", "MouseX: " + mouseX + " MouseY: " + mouseY);

            try {
                if(buildingManagerSystem.removeBuilding(mouseX, mouseY)){
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building removed");
                } else {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building not removed");
                }
            } catch (Exception e) {
                Gdx.app.error("[ERROR] - GameScreen", "Error removing building");
            }
        }
    }
}
