package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.audio.SoundSystem;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.input.GameInputProcessor;
import com.zhaw.frontier.systems.*;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.CameraControlSystem;
import com.zhaw.frontier.systems.EnemyManagementSystem;
import com.zhaw.frontier.systems.IdleBehaviourSystem;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.systems.MovementSystem;
import com.zhaw.frontier.systems.PatrolBehaviourSystem;
import com.zhaw.frontier.systems.RenderSystem;
import com.zhaw.frontier.ui.BaseUI;
import com.zhaw.frontier.ui.ResourceUI;
import com.zhaw.frontier.util.ButtonClickObserver;
import com.zhaw.frontier.util.GameMode;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import java.util.Map;

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
    private BaseUI baseUI;
    private CameraControlSystem cameraControlSystem;

    private OrthogonalTiledMapRenderer renderer;

    private TiledMapTileLayer sampleLayer;

    // UI
    private ResourceUI resourceUI;
    private Skin skin;
    private InventoryComponent inventory;
    private ResourceProductionSystem resourceProductionSystem;

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        this.renderer = new OrthogonalTiledMapRenderer(null, spriteBatchWrapper.getBatch());
        baseUI = new BaseUI(frontierGame, spriteBatchWrapper, this);
        baseUI.addObserver(this);
        this.engine = new Engine();

        this.gameWorldView = new ExtendViewport(16, 9);
        this.gameWorldView.getCamera().position.set(8, 4.5f, 0);
        this.gameWorldView.getCamera().update();

        this.cameraControlSystem = new CameraControlSystem(gameWorldView, engine, renderer);
    }

    @Override
    public void show() {
        Gdx.app.setLogLevel(this.frontierGame.getAppConfig().getLogLevel());
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

        engine.addSystem(new SoundSystem());

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

        // create stock entity
        Entity stock = engine.createEntity();
        stock.add(new InventoryComponent());
        engine.addEntity(stock);

        // setup resource tracking system
        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Resource Tracking System.");

        // TODO: remove this line, when the resource production system is implemented
        //ResourceProductionSystem resourceProductionSystem = new ResourceProductionSystem(engine);
        resourceProductionSystem = new ResourceProductionSystem(engine);

        engine.addSystem(resourceProductionSystem);
        Gdx.app.debug("[DEBUG] - GameScreen", "Resource Tracking System initialized.");

        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        // create resource ui
        skin = frontierGame.getAssetManager().get("skins/skin.json", Skin.class);
        resourceUI = new ResourceUI(skin, stage);

        engine.addSystem(new IdleBehaviourSystem());
        engine.addSystem(new PatrolBehaviourSystem());
        engine.addSystem(new MovementSystem());

        // create inventory ui
        Entity inventoryEntity = engine
            .getEntitiesFor(Family.all(InventoryComponent.class).get())
            .first();
        inventory = inventoryEntity.getComponent(InventoryComponent.class);

        var mx = new InputMultiplexer();
        mx.addProcessor(baseUI.getStage());
        if (cameraControlSystem != null) {
            mx.addProcessor(cameraControlSystem.getInputAdapter());
        }
        mx.addProcessor(stage);
        mx.addProcessor(new GameInputProcessor(engine, frontierGame));
        mx.addProcessor(baseUI.createInputAdapter(engine));
        Gdx.input.setInputProcessor(mx);
        //***********************************
        // Test Inventory for testing resource production for wood
        /*
        Entity testLumberMill = engine.createEntity();
        ResourceProductionComponent production = new ResourceProductionComponent();
        production.productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 2); // 2 Holz pro angrenzende Ressource
        production.countOfAdjacentResources = 3; // simuliert 3 angrenzende Wald-Tiles
        testLumberMill.add(production);
        engine.addEntity(testLumberMill);
        */
        //***********************************
    }

    @Override
    public void render(float delta) {
        handleInput();
        engine.update(delta);
        updateUI();
        baseUI.render(delta);
    }

    void handleInput() {
        // TODO handle other input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            frontierGame.switchScreen(new StartScreen(frontierGame));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            frontierGame.switchScreen(new PauseScreen(frontierGame, this));
        }
        //***********************************
        // Simulate resource production -> Temporary for testing
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Simulate end of turn
            resourceProductionSystem.endTurn();
        }
        //***********************************

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            SoundSystem soundSystem = engine.getSystem(SoundSystem.class);
            if (soundSystem != null) {
                soundSystem.playClick();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        gameUi.update(width, height);
        gameWorldView.update(width, height);
        baseUI.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private void updateUI() {
        gameUi.apply();
        stage.act();
        stage.draw();

        Map<ResourceTypeEnum, Integer> income = resourceProductionSystem.getProjectedIncome();

        int wood = inventory.resources.getOrDefault(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 0);
        int woodIncome = income.getOrDefault(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 0);
        int stone = inventory.resources.getOrDefault(ResourceTypeEnum.RESOURCE_TYPE_STONE, 0);
        int stoneIncome = income.getOrDefault(ResourceTypeEnum.RESOURCE_TYPE_STONE, 0);
        int iron = inventory.resources.getOrDefault(ResourceTypeEnum.RESOURCE_TYPE_IRON, 0);
        int ironIncome = income.getOrDefault(ResourceTypeEnum.RESOURCE_TYPE_IRON, 0);

        resourceUI.updateResources(wood, woodIncome, stone, stoneIncome, iron, ironIncome);
    }

    @Override
    public void buttonClicked(GameMode gameMode) {
        if (baseUI.getGameMode() == gameMode) {
            baseUI.setGameMode(GameMode.NORMAL);
        } else {
            baseUI.setGameMode(gameMode);
        }
    }
}
