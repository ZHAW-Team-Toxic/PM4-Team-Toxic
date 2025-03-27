package com.zhaw.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.BuildingUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test class for verifying the functionality of the {@link BuildingManagerSystem}.
 * <p>
 * This class tests various building placement scenarios such as placement on buildable tiles,
 * non-buildable tiles, resource tiles, and occupied tiles. The test map environment is set up
 * using the {@link TestMapEnvironment} helper class, which encapsulates map loading and configuration.
 * </p>
 * <p>
 * For detailed map configuration and tile type information, please refer to {@link TestMapEnvironment}.
 * </p>
 *
 * @see TestMapEnvironment
 * @see BuildingManagerSystem
 * @see TowerFactory
 * @see BuildingUtils
 */
@ExtendWith(GdxExtension.class)
public class BuildingManagerTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static TestMapEnvironment testMapEnvironment;

    /**
     * Converts a tile X coordinate to a screen coordinate.
     *
     * @param tileCoord the tile coordinate.
     * @return the corresponding screen X coordinate.
     */
    private static float tileToScreenX(float tileCoord) {
        return tileCoord * TestMapEnvironment.TILE_SIZE;
    }

    /**
     * Converts a tile Y coordinate to a screen coordinate.
     * <p>
     * This method corrects for the inverted Y axis caused by the difference between Tiled's top-left
     * origin and libGDX's bottom-left origin.
     * </p>
     *
     * @param tileCoord the tile Y coordinate.
     * @return the corresponding screen Y coordinate.
     */
    private static float tileToScreenY(float tileCoord) {
        return -tileCoord * TestMapEnvironment.TILE_SIZE;
    }

    /**
     * Sets up the test environment by initializing the test map, engine, and viewport.
     * <p>
     * The environment is established using the {@link TestMapEnvironment} helper class.
     * </p>
     */
    @BeforeAll
    public static void setUp() {
        testMapEnvironment = new TestMapEnvironment();
        testEngine = testMapEnvironment.getTestEngine();
        gameWorldView = testMapEnvironment.getViewport();

        addSystemsUnderTestHere();
        checkIfScreenConversionWorks();
    }

    // Add the BuildingManagerSystem to the test engine.
    private static void addSystemsUnderTestHere() {
        testEngine.addSystem(
            new BuildingManagerSystem(
                testMapEnvironment.getBottomLayer(),
                gameWorldView,
                testEngine
            )
        );
    }

    /**
     * Tests that a building can be placed on a buildable tile.
     * <p>
     * A tower is created and positioned at the screen coordinates corresponding to tile (5,5).
     * The {@link BuildingManagerSystem} is used to place the building and the test asserts that
     * the placement is successful.
     * </p>
     */
    @Test
    public void testBuildingPlacementOnBuildableTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (5,5)
        bp.position.x = tileToScreenX(5);
        bp.position.y = tileToScreenY(5);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");
    }

    /**
     * Tests that a building is not placed on a non-buildable tile.
     * <p>
     * A tower is created and positioned at the screen coordinates corresponding to tile (2,2),
     * which is non-buildable. The test asserts that the building is not placed.
     * </p>
     */
    @Test
    public void testBuildingNotPlacedOnNonBuildableTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (2,2) which is non-buildable
        bp.position.x = tileToScreenX(2);
        bp.position.y = tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower),
            "Building should not be placed on non-buildable tile."
        );
    }

    /**
     * Tests that a building is not placed on a resource tile.
     * <p>
     * A tower is created and positioned at the screen coordinates corresponding to tile (0,4),
     * which is designated as a resource tile. The test asserts that the building is not placed.
     * </p>
     */
    @Test
    public void testBuildingNotPlacedOnResourceTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (0,4) which is a resource tile
        bp.position.x = tileToScreenX(0);
        bp.position.y = tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(bms.placeBuilding(tower), "Building should not be placed on resource tile.");
    }

    /**
     * Tests that a building cannot be placed on an already occupied tile.
     * <p>
     * A tower is placed at the screen coordinates corresponding to tile (4,4).
     * A second tower is then attempted to be placed on the same tile.
     * The test asserts that the first placement is successful and the second placement fails.
     * </p>
     */
    @Test
    public void testBuildingNotPlacedOnOccupiedTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (4,4)
        bp.position.x = tileToScreenX(4);
        bp.position.y = tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");

        Entity tower2 = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        // Set the same screen coordinates for the second tower
        bp2.position.x = tileToScreenX(4);
        bp2.position.y = tileToScreenY(4);

        assertFalse(bms.placeBuilding(tower2), "Building should not be placed on occupied tile.");
    }

    /**
     * Tears down the test environment by removing all entities from the test engine.
     */
    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
        testMapEnvironment.dispose();
    }

    /**
     * Helper method to validate screen coordinate conversion.
     * <p>
     * This method verifies that converting a tile coordinate (5,5) to screen coordinates and then
     * unprojecting those coordinates via {@link BuildingUtils#calculateWorldCoordinate(Viewport, TiledMapTileLayer, float, float)}
     * yields the original tile coordinates (5,5).
     * </p>
     */
    private static void checkIfScreenConversionWorks() {
        // Convert tile (5,5) to screen coordinates.
        int screenX = (int) tileToScreenX(5);
        int screenY = (int) tileToScreenY(5);
        // Retrieve world coordinates from the bottom layer using BuildingUtils.
        float worldX = BuildingUtils.calculateWorldCoordinate(
            gameWorldView,
            testMapEnvironment.getBottomLayer(),
            screenX,
            screenY
        )
            .x;
        float worldY = BuildingUtils.calculateWorldCoordinate(
            gameWorldView,
            testMapEnvironment.getBottomLayer(),
            screenX,
            screenY
        )
            .y;
        // Divide by TILE_SIZE to get the tile coordinates; should equal 5.
        assertEquals(5, worldX, "World coordinate x should be 5");
        assertEquals(5, worldY, "World coordinate y should be 5");
    }
}
