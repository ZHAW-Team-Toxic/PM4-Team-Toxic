package com.zhaw.frontier.input;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.FrontierGame;
import com.zhaw.frontier.components.AnimationQueueComponent;
import com.zhaw.frontier.components.EnemyAnimationComponent;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.configs.AppConfig;
import com.zhaw.frontier.entityFactories.*;
import com.zhaw.frontier.enums.AppEnvironment;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.EnemySpawnSystem;
import com.zhaw.frontier.utils.QueueAnimation;
import com.zhaw.frontier.utils.WorldCoordinateUtils;

/**
 * A placeholder input processor for handling game input via keyboard.
 * <p>
 * This class currently binds building placement and removal actions to specific key events.
 * In the future, the UI will interface with this logic, and more advanced keyboard support
 * may be implemented. For now, key bindings are used to directly trigger actions.
 * </p>
 */
public class GameInputProcessor extends InputAdapter {

    private final Engine engine;
    private final FrontierGame frontierGame;
    private final AppConfig appConfig;

    private Viewport viewport;

    private int round = 15;

    /**
     * Constructs a new GameInputProcessor.
     *
     * @param engine        the {@link Engine} used for managing entities and systems.
     * @param frontierGame  the main {@link FrontierGame} instance.
     */
    public GameInputProcessor(Engine engine, FrontierGame frontierGame, Viewport viewport) {
        this.engine = engine;
        this.frontierGame = frontierGame;
        this.appConfig = frontierGame.getAppConfig();
        this.viewport = viewport;
    }

    /**
     * Processes key down events to perform various game actions during development mode.
     * <p>
     * Supported key actions:
     * <ul>
     *   <li>{@code B}: Places a tower at the current mouse coordinates.</li>
     *   <li>{@code N}: Places a wall at the current mouse coordinates.</li>
     *   <li>{@code M}: Places a resource building at the current mouse coordinates.</li>
     *   <li>{@code R}: Removes a building at the current mouse coordinates.</li>
     *   <li>{@code E}: Spawns a basic enemy at the current mouse coordinates.</li>
     *   <li>{@code I}: Spawns an idle enemy at the current mouse coordinates.</li>
     * </ul>
     * <p>
     * These debug actions are only enabled in the {@link com.zhaw.frontier.enums.AppEnvironment#DEV} environment.
     * </p>
     *
     * @param keycode the code of the key that was pressed
     * @return true if the key event was handled, false otherwise
     */
    @Override
    public boolean keyDown(int keycode) {
        // Retrieve the BuildingManagerSystem from the engine.
        BuildingManagerSystem buildingManagerSystem = engine.getSystem(BuildingManagerSystem.class);
        if (buildingManagerSystem == null) {
            Gdx.app.error("GameInputProcessor", "BuildingManagerSystem not found in engine!");
            return false;
        }

        if (this.appConfig.getEnvironment() != AppEnvironment.DEV) {
            return false;
        }

        // Get current mouse coordinates.
        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();

        // Place a tower if B is pressed.
        if (keycode == Input.Keys.B) {
            Gdx.app.debug(
                "GameInputProcessor",
                "B pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            try {
                Entity tower = TowerFactory.createBallistaTower(engine, mouseX, mouseY);
                if (buildingManagerSystem.placeBuilding(tower)) {
                    Gdx.app.debug("GameInputProcessor", "Tower placed successfully");
                } else {
                    Gdx.app.debug("GameInputProcessor", "Tower could not be placed");
                }
            } catch (Exception e) {
                Gdx.app.error("GameInputProcessor", "Error placing tower", e);
            }
            return true;
        }

        // Place a tower if B is pressed.
        if (keycode == Input.Keys.H) {
            Gdx.app.debug(
                "GameInputProcessor",
                "B pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            try {
                Entity hq = HQFactory.createSandClockHQ(engine, mouseX, mouseY);
                if (buildingManagerSystem.placeBuilding(hq)) {
                    Gdx.app.debug("GameInputProcessor", "Hq placed successfully");
                } else {
                    Gdx.app.debug("GameInputProcessor", "Hq could not be placed");
                }
            } catch (Exception e) {
                Gdx.app.error("GameInputProcessor", "Error placing hq", e);
            }
            return true;
        }

        if (keycode == Input.Keys.E) {
            Gdx.app.debug(
                "GameInputProcessor",
                "E pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            Entity enemyBasic = EnemyFactory.createPatrolEnemy(mouseX, mouseY);
            PositionComponent pos = enemyBasic.getComponent(PositionComponent.class);
            BottomLayerComponent bottomLayerComponent = engine
                .getEntitiesFor(Family.one(BottomLayerComponent.class).get())
                .get(0)
                .getComponent(BottomLayerComponent.class);
            pos.basePosition =
            WorldCoordinateUtils.calculateWorldCoordinate(
                viewport,
                bottomLayerComponent.bottomLayer,
                mouseX,
                mouseY
            );
            engine.addEntity(enemyBasic);
            return true;
        }

        if (keycode == Input.Keys.I) {
            Gdx.app.debug(
                "GameInputProcessor",
                "I pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            Entity enemyIdle = EnemyFactory.createIdleEnemy(mouseX, mouseY);
            PositionComponent pos = enemyIdle.getComponent(PositionComponent.class);
            BottomLayerComponent bottomLayerComponent = engine
                .getEntitiesFor(Family.one(BottomLayerComponent.class).get())
                .get(0)
                .getComponent(BottomLayerComponent.class);
            pos.basePosition =
            WorldCoordinateUtils.calculateWorldCoordinate(
                viewport,
                bottomLayerComponent.bottomLayer,
                mouseX,
                mouseY
            );
            pos.lookingDirection.set(0, -1);
            QueueAnimation enemyAnim = new QueueAnimation();
            enemyAnim.animationType = EnemyAnimationComponent.EnemyAnimationType.ATTACK_DOWN;
            enemyAnim.timeLeft = 1f;
            enemyAnim.loop = false;
            AnimationQueueComponent queue = enemyIdle.getComponent(AnimationQueueComponent.class);
            queue.queue.add(enemyAnim);
            engine.addEntity(enemyIdle);
            return true;
        }

        // Place a wall if N is pressed.
        if (keycode == Input.Keys.N) {
            Gdx.app.debug(
                "GameInputProcessor",
                "N pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            try {
                Entity wall = WallFactory.createWoodWall(engine, mouseX, mouseY);
                if (buildingManagerSystem.placeBuilding(wall)) {
                    Gdx.app.debug("GameInputProcessor", "Wall placed successfully");
                } else {
                    Gdx.app.debug("GameInputProcessor", "Wall could not be placed");
                }
            } catch (Exception e) {
                Gdx.app.error("GameInputProcessor", "Error placing wall", e);
            }
            return true;
        }

        // Place a resource building if M is pressed.
        if (keycode == Input.Keys.M) {
            Gdx.app.debug(
                "GameInputProcessor",
                "M pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            try {
                Entity resourceBuilding = ResourceBuildingFactory.woodResourceBuilding(
                    engine,
                    mouseX,
                    mouseY
                );
                if (buildingManagerSystem.placeBuilding(resourceBuilding)) {
                    Gdx.app.debug("GameInputProcessor", "Resource building placed successfully");
                } else {
                    Gdx.app.debug("GameInputProcessor", "Resource building could not be placed");
                }
            } catch (Exception e) {
                Gdx.app.error("GameInputProcessor", "Error placing resource building", e);
            }
            return true;
        }

        // Remove a building if R is pressed.
        if (keycode == Input.Keys.R) {
            Gdx.app.debug(
                "GameInputProcessor",
                "R pressed. MouseX: " + mouseX + ", MouseY: " + mouseY
            );
            try {
                if (buildingManagerSystem.removeBuilding(mouseX, mouseY)) {
                    Gdx.app.debug("GameInputProcessor", "Building removed successfully");
                } else {
                    Gdx.app.debug("GameInputProcessor", "No building found at that location");
                }
            } catch (Exception e) {
                Gdx.app.error("GameInputProcessor", "Error removing building", e);
            }
            return true;
        }

        if (keycode == Input.Keys.P) {
            Entity stock = engine
                .getEntitiesFor(Family.all(InventoryComponent.class).get())
                .first();
            InventoryComponent inventory = stock.getComponent(InventoryComponent.class);

            Gdx.app.debug("GameInputProcessor", "Inventory: " + inventory.resources);
        }

        if (keycode == Input.Keys.L) {
            round++;
            EnemySpawnSystem enemySpawnManager = EnemySpawnSystem.create(engine);
            if (enemySpawnManager.spawnEnemies(round)) {
                Gdx.app.debug("GameInputProcessor", "Enemies spawned successfully");
            } else {
                Gdx.app.debug("GameInputProcessor", "Failed to spawn enemies");
            }
        }

        return false;
    }
}
