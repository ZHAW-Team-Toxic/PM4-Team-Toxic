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
import com.zhaw.frontier.components.EntityTypeComponent;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.enums.GameMode;
import com.zhaw.frontier.input.GameInputProcessor;
import com.zhaw.frontier.systems.*;
import com.zhaw.frontier.ui.BaseUI;
import com.zhaw.frontier.ui.BuildingMenuUi;
import com.zhaw.frontier.ui.ResourceUI;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.ButtonClickObserver;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;
import java.util.Map;
import lombok.Getter;

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

    @Getter
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
    private EnemyManagementSystem enemyManagementSystem;

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

        engine.addSystem(new IdleBehaviourSystem());
        engine.addSystem(new PatrolBehaviourSystem());

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Movement System.");
        engine.addSystem(new MovementSystem());
        Gdx.app.debug("[DEBUG] - GameScreen", "Movement System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Animation System.");
        engine.addSystem(new AnimationSystem());
        Gdx.app.debug("[DEBUG] - GameScreen", "Animation System initialized.");

        engine.addSystem(new SoundSystem());

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Building Manager System.");
        engine.addSystem(new BuildingManagerSystem(sampleLayer, gameWorldView, engine));
        EnemyManagementSystem.init(sampleLayer, gameWorldView, engine);
        enemyManagementSystem = EnemyManagementSystem.getInstance();
        Gdx.app.debug("[DEBUG] - GameScreen", "Building Manager System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Building Remover System.");
        engine.addSystem(enemyManagementSystem);
        Gdx.app.debug("[DEBUG] - GameScreen", "Building Remover System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Building Manager System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Camera Control System.");
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

        Gdx.app.debug("[DEBUG] - GameScreen", "Creating stock entity.");
        Entity stock = engine.createEntity();
        stock.add(new InventoryComponent());
        stock.add(new EntityTypeComponent(EntityTypeComponent.EntityType.INVENTORY));
        engine.addEntity(stock);
        Gdx.app.debug("[DEBUG] - GameScreen", "Stock entity created.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Resource Tracking System.");

        // TODO: remove this line, when the resource production system is implemented
        //ResourceProductionSystem resourceProductionSystem = new ResourceProductionSystem(engine);
        ResourceProductionSystem.init(engine);
        resourceProductionSystem = ResourceProductionSystem.getInstance();

        engine.addSystem(resourceProductionSystem);
        Gdx.app.debug("[DEBUG] - GameScreen", "Resource Tracking System initialized.");

        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());
        BuildingMenuUi buildingMenuUi = new BuildingMenuUi(engine, stage);
        baseUI.addObserver(buildingMenuUi);
        buildingMenuUi.addObserver(this);

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Render System.");
        //setup render system
        engine.addSystem(new RenderSystem(gameWorldView, engine, renderer));
        Gdx.app.debug("[DEBUG] - GameScreen", "Render System initialized.");
        // create resource ui
        skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);
        resourceUI = new ResourceUI(skin, stage);

        engine.addSystem(new IdleBehaviourSystem());
        engine.addSystem(new PatrolBehaviourSystem());

        // create inventory ui
        Entity inventoryEntity = engine
            .getEntitiesFor(Family.all(InventoryComponent.class).get())
            .first();
        inventory = inventoryEntity.getComponent(InventoryComponent.class);

        var mx = new InputMultiplexer();
        mx.addProcessor(baseUI.getStage());
        mx.addProcessor(stage);
        if (cameraControlSystem != null) {
            mx.addProcessor(cameraControlSystem.getInputAdapter());
        }
        mx.addProcessor(new GameInputProcessor(engine, frontierGame));
        mx.addProcessor(baseUI.createInputAdapter(engine));
        mx.addProcessor(buildingMenuUi.createInputAdapter(engine));

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
