package com.zhaw.frontier.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.BuildingPositionComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.entities.ResourceBuildings;
import com.zhaw.frontier.entities.Towers;
import com.zhaw.frontier.entities.Walls;
import com.zhaw.frontier.systems.BuildingManager;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.systems.CameraControlSystem;
import com.zhaw.frontier.systems.RenderSystem;
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

    private BuildingManager buildingManagerSystem;

    private OrthogonalTiledMapRenderer renderer;

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
        Gdx.app.debug("[DEBUG] - GameScreen", "Layer " +MapLoader.getInstance().getMapEntity().getComponent(BottomLayerComponent.class).bottomLayer.getName() + " loaded.");
        Gdx.app.debug("[DEBUG] - GameScreen", "Layer " +MapLoader.getInstance().getMapEntity().getComponent(DecorationLayerComponent.class).decorationLayer.getName() + " loaded.");
        Gdx.app.debug("[DEBUG] - GameScreen", "Layer " +MapLoader.getInstance().getMapEntity().getComponent(ResourceLayerComponent.class).resourceLayer.getName() + " loaded.");

        //set-up BuildingManager
        buildingManagerSystem = new BuildingManager(MapLoader.getInstance().getMapEntity().getComponent(BottomLayerComponent.class).bottomLayer, gameWorldView, engine);

        //setup render system
        engine.addSystem(
            new RenderSystem(
                gameWorldView,
                engine,
                renderer)
        );

        // setup camera
        CameraControlSystem cameraControlSystem = new CameraControlSystem(
            gameWorldView,
            engine,
            renderer
        );
        engine.addSystem(cameraControlSystem);

        // create game ui
        gameUi = new ScreenViewport();
        stage = new Stage(gameUi, spriteBatchWrapper.getBatch());

        var mx = new InputMultiplexer();
        mx.addProcessor(cameraControlSystem.getInputAdapter());
        mx.addProcessor(stage);
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
                Entity tower = Towers.createDefaultTower(engine);
                tower.getComponent(BuildingPositionComponent.class).position.x = mouseX;
                tower.getComponent(BuildingPositionComponent.class).position.y = mouseY;
                if (buildingManagerSystem.placeBuilding(tower)) {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building placed");
                } else {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building not placed");
                }
            } catch (Exception e) {
                Gdx.app.error("[ERROR] - GameScreen", "Error placing building");
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.N)){
            //TODO only temporary - will be handled by UI later
            Gdx.app.debug("[DEBUG] - GameScreen", "N pressed");
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Gdx.app.debug("[DEBUG] - GameScreen", "MouseX: " + mouseX + " MouseY: " + mouseY);

            try {
                Entity wall = Walls.createDefaultWall(engine);
                wall.getComponent(BuildingPositionComponent.class).position.x = mouseX;
                wall.getComponent(BuildingPositionComponent.class).position.y = mouseY;
                if (buildingManagerSystem.placeBuilding(wall)) {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building placed");
                } else {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building not placed");
                }
            } catch (Exception e) {
                Gdx.app.error("[ERROR] - GameScreen", "Error placing building");
            }
        }
        if(Gdx.input.isKeyJustPressed(Keys.M)){
            //TODO only temporary - will be handled by UI later
            Gdx.app.debug("[DEBUG] - GameScreen", "M pressed");
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Gdx.app.debug("[DEBUG] - GameScreen", "MouseX: " + mouseX + " MouseY: " + mouseY);
            try {
                Entity resourceBuilding = ResourceBuildings.createDefaultResourceBuilding(engine);
                resourceBuilding.getComponent(BuildingPositionComponent.class).position.x = mouseX;
                resourceBuilding.getComponent(BuildingPositionComponent.class).position.y = mouseY;
                if (buildingManagerSystem.placeBuilding(resourceBuilding)) {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building placed");
                } else {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building not placed");
                }
            }catch (Exception e){
                Gdx.app.error("[ERROR] - GameScreen", "Error placing building");
            }
        }
        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            Gdx.app.debug("[DEBUG] - GameScreen", "R pressed");
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Gdx.app.debug("[DEBUG] - GameScreen", "MouseX: " + mouseX + " MouseY: " + mouseY);

            try {
                if (buildingManagerSystem.removeBuilding(mouseX, mouseY)) {
                    Gdx.app.debug("[DEBUG] - GameScreen", "Building removed");
                } else {
                    Gdx.app.debug("[DEBUG] - GameScreen", "There was no building to remove");
                }
            } catch (Exception e) {
                Gdx.app.error("[ERROR] - GameScreen", "Error removing building");
            }
        }
    }
}
