package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.algorithm.SimpleAStarPathfinder;
import com.zhaw.frontier.audio.SoundSystem;
import com.zhaw.frontier.components.EntityTypeComponent;
import com.zhaw.frontier.components.HQComponent;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.entityFactories.CursorFactory;
import com.zhaw.frontier.entityFactories.HQFactory;
import com.zhaw.frontier.enums.GameMode;
import com.zhaw.frontier.input.GameInputProcessor;
import com.zhaw.frontier.systems.*;
import com.zhaw.frontier.systems.StateDirectionalTextureSystem;
import com.zhaw.frontier.systems.TowerTargetingSystem;
import com.zhaw.frontier.systems.behaviour.IdleBehaviourSystem;
import com.zhaw.frontier.systems.behaviour.PatrolBehaviourSystem;
import com.zhaw.frontier.systems.building.BuildingManagerSystem;
import com.zhaw.frontier.systems.movement.BlockingMovementSystem;
import com.zhaw.frontier.systems.movement.MovementSystem;
import com.zhaw.frontier.systems.movement.PathFollowerSystem;
import com.zhaw.frontier.systems.movement.PathfindingSystem;
import com.zhaw.frontier.systems.movement.SteeringMovementSystem;
import com.zhaw.frontier.ui.BaseUI;
import com.zhaw.frontier.ui.BuildingMenuUi;
import com.zhaw.frontier.ui.ResourceUI;
import com.zhaw.frontier.utils.AssetManagerInstance;
import com.zhaw.frontier.utils.ButtonClickObserver;
import com.zhaw.frontier.utils.EnemySpawner;
import com.zhaw.frontier.wrappers.SpriteBatchInterface;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.Getter;

import static com.zhaw.frontier.systems.building.BuildingPlacer.occupyTile;

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

    public GameScreen(FrontierGame frontierGame) {
        this.frontierGame = frontierGame;
        this.spriteBatchWrapper = frontierGame.getBatch();
        Gdx.graphics.setCursor(CursorFactory.createDefaultCursor());
        this.renderer = new OrthogonalTiledMapRenderer(null, spriteBatchWrapper.getBatch());
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
        Gdx.app.debug("GameScreen", "Initializing the engine.");

        // set-up Map
        MapLoader.getInstance().initMapLayerEntities(engine);
        Gdx.app.debug("GameScreen", "Initializing sample layer.");
        // init sample layer as base for the map width / map height
        sampleLayer =
        MapLoader.getInstance().getMapEntity().getComponent(BottomLayerComponent.class).bottomLayer;
        Gdx.app.debug(
            "GameScreen",
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
        engine.addSystem(new EnemyAttackSystem());
        engine.addSystem(new EnemyAttackAnimationSystem());
        engine.addSystem(new HealthSystem());
        engine.addSystem(new DeathSystem());
        engine.addSystem(new BlockingMovementSystem());
        var rangeVisual = new RangeVisualSystem(sampleLayer);
        engine.addSystem(rangeVisual);

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Movement System.");
        engine.addSystem(new PathFollowerSystem());
        engine.addSystem(new SteeringMovementSystem());
        engine.addSystem(new MovementSystem());
        engine.addSystem(new AnimationSystem());
        Gdx.app.debug("[DEBUG] - GameScreen", "Animation System initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing win condition system.");
        engine.addSystem(new WinConditionSystem(frontierGame));
        Gdx.app.debug("[DEBUG] - GameScreen", "win condition system initialized.");

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing lose condition system.");
        engine.addSystem(new LoseConditionSystem(frontierGame));
        Gdx.app.debug("[DEBUG] - GameScreen", "Lost condition system initialized.");

        engine.addSystem(new StateDirectionalTextureSystem());
        engine.addSystem(new ProjectileCollisionSystem());

        engine.addSystem(new SoundSystem());
        engine.addSystem(new TowerTargetingSystem());
        engine.addSystem(new CooldownSystem());

        engine.addSystem(new BuildingManagerSystem(sampleLayer, gameWorldView, engine));

        engine.addSystem(cameraControlSystem);

        initHq(engine, sampleLayer);

        Gdx.app.debug("GameScreen", "Creating stock entity.");
        Entity stock = engine.createEntity();
        stock.add(new InventoryComponent());
        stock.add(new EntityTypeComponent(EntityTypeComponent.EntityType.INVENTORY));
        engine.addEntity(stock);

        Gdx.app.debug("GameScreen", "Initializing Resource Tracking System.");

        ResourceProductionSystem.init(engine);
        resourceProductionSystem = ResourceProductionSystem.getInstance();
        engine.addSystem(resourceProductionSystem);

        baseUI = new BaseUI(frontierGame, spriteBatchWrapper, this);
        baseUI.addObserver(this);
        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());
        BuildingMenuUi buildingMenuUi = new BuildingMenuUi(engine, stage);
        baseUI.addObserver(buildingMenuUi);
        buildingMenuUi.addObserver(this);
        buildingMenuUi.addObserver(baseUI);

        Gdx.app.debug("GameScreen", "Initializing Render System.");
        // setup render system
        engine.addSystem(new RenderSystem(gameWorldView, engine, renderer));
        // create resource ui
        skin = AssetManagerInstance.getManager().get("skins/skin.json", Skin.class);
        resourceUI = new ResourceUI(skin, stage);

        ErrorSystem.init(stage, skin);

        SimpleAStarPathfinder pathfinder = new SimpleAStarPathfinder(
            MapLoader.getInstance().getAllWalkableLayers(),
            engine
        );
        PathfindingSystem pathfindingSystem = new PathfindingSystem(pathfinder);
        engine.addSystem(pathfindingSystem);

        Gdx.app.debug("[DEBUG] - GameScreen", "Initializing Enemy Spawn Manager.");
        EnemySpawnSystem.create(engine);
        EnemySpawner.getInstance().init(gameUi, engine);
        Gdx.app.debug("[DEBUG] - GameScreen", "Enemy Spawn Manager initialized.");

        // create inventory ui
        Entity inventoryEntity = engine
            .getEntitiesFor(Family.all(InventoryComponent.class).get())
            .first();
        inventory = inventoryEntity.getComponent(InventoryComponent.class);

        var mx = new InputMultiplexer();
        mx.addProcessor(stage);
        mx.addProcessor(baseUI.getStage());
        mx.addProcessor(rangeVisual.clickListener(gameWorldView));
        if (cameraControlSystem != null) {
            mx.addProcessor(cameraControlSystem.getInputAdapter());
        }
        mx.addProcessor(new GameInputProcessor(engine, frontierGame, gameWorldView));
        mx.addProcessor(baseUI.createInputAdapter(engine));
        mx.addProcessor(buildingMenuUi.createInputAdapter(engine));
        Gdx.input.setInputProcessor(mx);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1); // Black background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
            stage.dispose();
            baseUI.dispose();
            frontierGame.switchScreenWithoutDispose(new PauseScreen(frontierGame, this));
        }
        // ***********************************
        // Simulate resource production -> Temporary for testing
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Simulate end of turn
            resourceProductionSystem.endTurn();
        }
        // ***********************************

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

        ArrayList<EntitySystem> systemsCopy = new ArrayList<>((Collection) engine.getSystems());
        for (EntitySystem system : systemsCopy) {
            engine.removeSystem(system);
        }

        stage.dispose();
        baseUI.dispose();
    }

    @Override
    public void dispose() {
        engine.removeAllEntities();

        ArrayList<EntitySystem> systemsCopy = new ArrayList<>((Collection) engine.getSystems());
        for (EntitySystem system : systemsCopy) {
            engine.removeSystem(system);
        }

        stage.dispose();
        baseUI.dispose();
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
            Gdx.graphics.setCursor(CursorFactory.createDefaultCursor());
        } else if (gameMode == GameMode.DEMOLISH) {
            Gdx.graphics.setCursor(CursorFactory.createDeleteCursor());
        } else if (gameMode == GameMode.BUILDING) {
            Gdx.graphics.setCursor(CursorFactory.createBuildingCursor());
        }
    }

    private void initHq(Engine engine, TiledMapTileLayer tiledMapTileLayer) {
        float centerX = (tiledMapTileLayer.getWidth() / 2f) + 0.5f;
        float centerY = (tiledMapTileLayer.getHeight() / 2f) + 0.5f;

        boolean hasHq = engine.getEntitiesFor(Family.all(HQComponent.class).get()).size() > 0;

        if (!hasHq) {
            Entity entity = HQFactory.createSandClockHQ(engine, centerX, centerY);
            occupyTile(entity);
            engine.addEntity(entity);
        }
    }
}
