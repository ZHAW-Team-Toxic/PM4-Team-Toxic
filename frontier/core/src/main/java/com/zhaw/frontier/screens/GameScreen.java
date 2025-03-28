package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entityFactories.WallFactory;
import com.zhaw.frontier.input.GameInputProcessor;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.CameraControlSystem;
import com.zhaw.frontier.systems.EnemyManagementSystem;
import com.zhaw.frontier.systems.IdleBehaviourSystem;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.systems.PatrolBehaviourSystem;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.util.ButtonClickObserver;
import com.zhaw.frontier.util.GameMode;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

/**
 * Initializes all components, systems, ui elements, and viewports needed to
 * render the game.
 * Controls the handling of user input, rendering and game logic during each
 * render.
 */
public class GameScreen implements Screen, ButtonClickObserver {

    private FrontierGame frontierGame;
    private SpriteBatchInterface spriteBatchWrapper;
    private ExtendViewport gameWorldView;
    private ScreenViewport gameUi;
    private Stage stage;
    private Engine engine;
    private GameUIScreen gameUIScreen;
    private GameMode gameMode = GameMode.NORMAL;
    private CameraControlSystem cameraControlSystem;

    private OrthogonalTiledMapRenderer renderer;

    private TiledMapTileLayer sampleLayer;

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        this.renderer = new OrthogonalTiledMapRenderer(null, spriteBatchWrapper.getBatch());
        frontierGame.getAssetManager().load("skins/skin.json", Skin.class);
        frontierGame.getAssetManager().finishLoading();
        gameUIScreen = new GameUIScreen(frontierGame, spriteBatchWrapper);
        gameUIScreen.addObserver(this);
        this.engine = new Engine();

        this.gameWorldView = new ExtendViewport(16, 9);
        this.gameWorldView.getCamera().position.set(8, 4.5f, 0);
        this.gameWorldView.getCamera().update();

        this.cameraControlSystem = new CameraControlSystem(gameWorldView, engine, renderer);
    }

    @Override
    public void show() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // setup up ecs(entity component system)
        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing the engine.");

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
        engine.addSystem(new EnemyManagementSystem(sampleLayer, gameWorldView, engine));
        Gdx.app.debug("[DEBUG] - GameScreen", "Building Manager System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Render System.");
        //setup render system
        engine.addSystem(new RenderSystem(gameWorldView, engine, renderer));
        Gdx.app.debug("[DEBUG] - GameScreen", "Render System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Camera Control System.");
        // setup camera

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

        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        engine.addSystem(new IdleBehaviourSystem());
        engine.addSystem(new PatrolBehaviourSystem());
        engine.addSystem(new MovementSystem());

        var mx = new InputMultiplexer();
        mx.addProcessor(gameUIScreen.getStage());
        mx.addProcessor(cameraControlSystem.getInputAdapter());
        if (cameraControlSystem != null) {
            mx.addProcessor(cameraControlSystem.getInputAdapter());
        }
        mx.addProcessor(stage);
        mx.addProcessor(new GameInputProcessor(engine, frontierGame));
        Gdx.input.setInputProcessor(mx);

        mx.addProcessor(
            new InputAdapter() {
                @Override
                public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                    if (gameMode == GameMode.DEMOLISH) {
                        engine
                            .getSystem(BuildingManagerSystem.class)
                            .removeBuilding(screenX, screenY);
                    } else if (gameMode == GameMode.BUILDING) {
                        Entity entity = WallFactory.createDefaultWall(engine);
                        entity.getComponent(PositionComponent.class).position =
                        new Vector2(screenX, screenY);
                        engine.getSystem(BuildingManagerSystem.class).placeBuilding(entity);
                    }
                    return true;
                }
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        handleInput();
        engine.update(delta);
        updateUI();
        gameUIScreen.render(delta);
    }

    private void handleInput() {
        // TODO handle other input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            frontierGame.switchScreen(new StartScreen(frontierGame));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            frontierGame.switchScreen(new PauseScreen(frontierGame, this));
        }
    }

    @Override
    public void resize(int width, int height) {
        gameUi.update(width, height);
        gameWorldView.update(width, height);
        gameUIScreen.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        gameUIScreen.dispose();
    }

    private void updateUI() {
        gameUi.apply();
        stage.act();
        stage.draw();
    }

    @Override
    public void buttonClicked(GameMode gameMode) {
        if (this.gameMode == gameMode) {
            this.gameMode = GameMode.NORMAL;
        } else {
            this.gameMode = gameMode;
        }
    }
}
