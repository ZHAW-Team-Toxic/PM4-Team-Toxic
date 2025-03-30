package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.input.GameInputProcessor;
import com.zhaw.frontier.systems.*;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

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

    private OrthogonalTiledMapRenderer renderer;

    private TiledMapTileLayer sampleLayer;

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        this.renderer = new OrthogonalTiledMapRenderer(null, spriteBatchWrapper.getBatch());

        Gdx.app.setLogLevel(Application.LOG_DEBUG);

        // create view with world coordinates
        gameWorldView = new ExtendViewport(16, 9);

        // setup up ecs(entity component system)
        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing the engine.");
        engine = new Engine();

        //set-up Map
        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Map Layer Entities.");
        MapLoader.getInstance().initMapLayerEntities(engine);
        Gdx.app.debug(
            "[DEBUG] - GameScreen",
            "Layer " +
            MapLoader
                .getInstance()
                .getMapEntity()
                .getComponent(BottomLayerComponent.class)
                .bottomLayer.getName() +
            " loaded."
        );
        Gdx.app.debug(
            "[DEBUG] - GameScreen",
            "Layer " +
            MapLoader
                .getInstance()
                .getMapEntity()
                .getComponent(DecorationLayerComponent.class)
                .decorationLayer.getName() +
            " loaded."
        );
        Gdx.app.debug(
            "[DEBUG] - GameScreen",
            "Layer " +
            MapLoader
                .getInstance()
                .getMapEntity()
                .getComponent(ResourceLayerComponent.class)
                .resourceLayer.getName() +
            " loaded."
        );

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing sample layer.");
        //init sample layer  as base for the map width / map height
        sampleLayer =
        MapLoader.getInstance().getMapEntity().getComponent(BottomLayerComponent.class).bottomLayer;
        Gdx.app.debug(
            "[DEBUG] - GameScreen",
            "Sample Layer loaded. Map width: " +
            sampleLayer.getWidth() +
            " Map height: " +
            sampleLayer.getHeight() +
            " Tile width: " +
            sampleLayer.getTileWidth() +
            " Tile height: " +
            sampleLayer.getTileHeight()
        );

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Building Manager System.");
        //set-up BuildingManager
        engine.addSystem(new BuildingManagerSystem(sampleLayer, gameWorldView, engine));
        Gdx.app.debug("[DEBUG] - GameScreen", "Building Manager System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Render System.");
        //setup render system
        engine.addSystem(new RenderSystem(gameWorldView, engine, renderer));
        Gdx.app.debug("[DEBUG] - GameScreen", "Render System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Camera Control System.");
        // setup camera
        CameraControlSystem cameraControlSystem = new CameraControlSystem(
            gameWorldView,
            engine,
            renderer
        );
        engine.addSystem(cameraControlSystem);
        Gdx.app.debug(
            "[DEBUG] - GameScreen",
            "Camera Control System initialized." +
            " Camera position: " +
            ((OrthographicCamera) cameraControlSystem.getCamera()).position.x +
            " x " +
            ((OrthographicCamera) cameraControlSystem.getCamera()).position.y +
            " y" +
            " Camera zoom: " +
            ((OrthographicCamera) cameraControlSystem.getCamera()).zoom
        );

        // create stock entity
        Entity stock = engine.createEntity();
        stock.add(new InventoryComponent());
        engine.addEntity(stock);

        // setup resource tracking system
        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Resource Tracking System.");
        ResourceProductionSystem resourceProductionSystem = new ResourceProductionSystem(engine);
        engine.addSystem(resourceProductionSystem);
        Gdx.app.debug("[DEBUG] - GameScreen", "Resource Tracking System initialized.");


        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        var mx = new InputMultiplexer();
        mx.addProcessor(cameraControlSystem.getInputAdapter());
        mx.addProcessor(stage);
        mx.addProcessor(new GameInputProcessor(engine, frontierGame));
        Gdx.input.setInputProcessor(mx);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
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
}
