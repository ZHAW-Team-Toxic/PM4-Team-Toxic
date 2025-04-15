package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.InventoryComponent;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.ResourceTypeEnum;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.ResourceProductionSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Integration tests for verifying the resource production and tracking system.
 *
 * <p>This test suite uses a test map and simulates the placement of resource-producing buildings.
 * It verifies whether the correct resources are collected and stored in the global inventory based
 * on adjacent resource tiles and production rate configuration.</p>
 *
 * <p>The building placement uses screen-to-world coordinate conversion,
 * and validates adjacency to resource tiles using {@link BuildingManagerSystem} and {@code ResourceAdjacencyChecker}.</p>
 *
 * @see ResourceProductionSystem
 * @see BuildingManagerSystem
 * @see ResourceProductionComponent
 * @see InventoryComponent
 * @see TestMapEnvironment
 */
@ExtendWith(GdxExtension.class)
public class ResourceTrackingSystemTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;

    /**
     * Sets up the test environment by initializing the test map, engine, and viewport.
     *
     * <p>The environment is configured using {@link TestMapEnvironment}, which loads a 9x9 test TMX map
     * with predefined tile properties for buildability and resource types.</p>
     */
    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getGameWorldView();

        addSystemsUnderTestHere();
    }

    /**
     * Registers the systems that are under test.
     *
     * <p>This includes:
     * <ul>
     *     <li>{@link BuildingManagerSystem} – handles building placement</li>
     *     <li>{@link ResourceProductionSystem} – processes resource generation per turn</li>
     * </ul>
     * A global inventory entity is also added to the engine.
     */
    private static void addSystemsUnderTestHere() {
        testEngine.addSystem(
            new BuildingManagerSystem(
                testMapEnvironment.getBottomLayer(),
                gameWorldView,
                testEngine
            )
        );
        ResourceProductionSystem.init(testEngine);
        testEngine.addSystem(ResourceProductionSystem.getInstance());

        Entity stock = testEngine.createEntity();
        stock.add(new InventoryComponent());
        testEngine.addEntity(stock);
    }

    /**
     * Tests that a wood-producing building at tile (4,2) successfully collects wood resources.
     *
     * <p>
     * Expected: After placing the building and running a system update, the inventory should
     * contain a positive amount of {@link ResourceTypeEnum#RESOURCE_TYPE_WOOD}.
     * </p>
     */
    @Test
    public void testResourceTrackingWood() {
        Entity building = ResourceBuildingFactory.woodResourceBuilding(testEngine);
        building.getComponent(PositionComponent.class).position.x = tileToScreenX(4);
        building.getComponent(PositionComponent.class).position.y = tileToScreenY(2);
        building
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 1);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(building), "Building should be placed on buildable tile.");

        testEngine.update(0.1f);

        InventoryComponent inventory = testEngine
            .getEntitiesFor(Family.all(InventoryComponent.class).get())
            .first()
            .getComponent(InventoryComponent.class);
        int amount = inventory.resources.get(ResourceTypeEnum.RESOURCE_TYPE_WOOD);

        assertTrue(amount > 0, "Wood should be collected");
    }

    /**
     * Tests that a stone-producing building at tile (2,4) successfully collects stone resources.
     *
     * <p>
     * Expected: After placing the building and updating the engine, the inventory should contain
     * a positive amount of {@link ResourceTypeEnum#RESOURCE_TYPE_STONE}.
     * </p>
     */
    @Test
    public void testResourceTrackingStone() {
        Entity building = ResourceBuildingFactory.stoneResourceBuilding(testEngine);
        building.getComponent(PositionComponent.class).position.x = tileToScreenX(2);
        building.getComponent(PositionComponent.class).position.y = tileToScreenY(4);
        building
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_STONE, 1);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(building), "Building should be placed on buildable tile.");

        testEngine.update(0.1f);

        InventoryComponent inventory = testEngine
            .getEntitiesFor(Family.all(InventoryComponent.class).get())
            .first()
            .getComponent(InventoryComponent.class);
        int amount = inventory.resources.get(ResourceTypeEnum.RESOURCE_TYPE_STONE);

        assertTrue(amount > 0, "Stone should be collected");
    }

    /**
     * Tests that an iron-producing building at tile (6,4) successfully collects iron resources.
     *
     * <p>
     * Expected: The building has adjacent iron tiles, and after system update, the inventory should
     * contain a positive amount of {@link ResourceTypeEnum#RESOURCE_TYPE_IRON}.
     * </p>
     */
    @Test
    public void testResourceTrackingIron() {
        Entity building = ResourceBuildingFactory.ironResourceBuilding(testEngine);
        building.getComponent(PositionComponent.class).position.x = tileToScreenX(6);
        building.getComponent(PositionComponent.class).position.y = tileToScreenY(4);
        building
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_IRON, 1);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(building), "Building should be placed on buildable tile.");

        testEngine.update(0.1f);

        InventoryComponent inventory = testEngine
            .getEntitiesFor(Family.all(InventoryComponent.class).get())
            .first()
            .getComponent(InventoryComponent.class);
        int amount = inventory.resources.get(ResourceTypeEnum.RESOURCE_TYPE_IRON);

        assertTrue(amount > 0, "Iron should be collected");
    }

    /**
     * Tests that a wood-producing building with production rate 0 does not collect any wood.
     *
     * <p>
     * The building is placed at tile (4,4) where no adjacent wood resource is available.
     * It should be rejected by the {@link BuildingManagerSystem}.
     * </p>
     */
    @Test
    public void testResourceTrackingWoodShouldNotBeCollected() {
        Entity building = ResourceBuildingFactory.woodResourceBuilding(testEngine);
        building.getComponent(PositionComponent.class).position.x = tileToScreenX(4);
        building.getComponent(PositionComponent.class).position.y = tileToScreenY(4);
        building
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(ResourceTypeEnum.RESOURCE_TYPE_WOOD, 0);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(building),
            "Building should not be placed on a tile where there are no adjacent resources."
        );
    }

    /**
     * Cleans up the test environment after all tests have run.
     *
     * <p>Removes all entities from the engine and disposes of the map environment.</p>
     */
    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
        testMapEnvironment.dispose();
    }

    /**
     * Converts a tile X-coordinate to a screen-space coordinate (pixels).
     *
     * @param tileCoord the X tile coordinate
     * @return the screen-space X coordinate in pixels
     */
    private static float tileToScreenX(float tileCoord) {
        return tileCoord * TestMapEnvironment.TILE_SIZE;
    }

    /**
     * Converts a tile Y-coordinate to screen-space, compensating for Y-axis inversion.
     *
     * <p>
     * In libGDX, (0,0) is bottom-left, but in Tiled it's top-left. This method corrects that.
     * </p>
     *
     * @param tileCoord the Y tile coordinate
     * @return the screen-space Y coordinate in pixels (inverted)
     */
    private static float tileToScreenY(float tileCoord) {
        return -tileCoord * TestMapEnvironment.TILE_SIZE;
    }
}
