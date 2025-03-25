package com.zhaw.tests;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.PositionComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.entityFactories.TowerFactory;
import com.zhaw.frontier.systems.BuildingManagerSystem;
import com.zhaw.frontier.systems.BuildingUtils;
import com.zhaw.frontier.systems.MapLoader;
import java.nio.file.Path;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test class for the BuildingManagerSystem.
 * Tests the placement of buildings on the map.
 * This test uses the test map:
 * "TMX/frontier_map_for_tests.tmx"
 * It is structured like this:
 *
 * Bottom layer and Resource layer combined:
 *
 * 0,0,1,3,3,3,1,0,0,
 * 0,0,1,1,3,1,1,0,0,
 * 1,1,2,1,1,1,2,1,1,
 * 4,1,1,1,1,1,1,1,5,
 * 4,4,1,1,1,1,1,5,5,
 * 4,1,1,1,1,1,1,1,5,
 * 1,1,2,1,1,1,2,1,1,
 * 0,0,1,1,3,1,1,0,0,
 * 0,0,1,3,3,3,1,0,0
 *
 * Spawn area: 0
 *  - Properties on Tile: isBuildable: {false}, isSpawnPoint: {true}, isTraversable: {true}
 * Buildable area: 1
 *  - Properties on Tile: isBuildable: {true}, isSpawnPoint: {false}, isTraversable: {true}
 * Water area: 2
 *  - Properties on Tile: isBuildable: {false}, isTraversable: {false}
 * Wood resource tile: 3
 *  - Properties on Tile: isBuildable: {false}, isTraversable: {true}, resourceType: {wood}
 * Stone resource tile: 4
 *  - Properties on Tile: isBuildable: {false}, isTraversable: {true}, resourceType: {stone}
 * Iron resource tile: 5
 *  - Properties on Tile: isBuildable: {false}, isTraversable: {true}, resourceType: {iron}
 *
 * Important:
 *  - The map is 9x9 tiles.
 *  - Tiles are 16 x 16 pixels.
 *  - (0,0) is the bottom left corner of the map.
 *    - Map gets rendered from bottom left corner.
 *
 * Note:
 *  - Due to differences in coordinate systems between Tiled (top-left origin) and libGDX (bottom-left origin),
 *    the Y axis is inverted. In earlier versions you had to pass a negative Y tile value (e.g. tileToScreen(–5))
 *    to compensate. To resolve this, the helper method {@link #tileToScreenY(float)} was added.
 *  - With this method, you can use positive tile coordinates for both X and Y.
 *    The method multiplies the Y value by –TILE_SIZE to automatically perform the necessary inversion.
 */
@ExtendWith(GdxExtension.class)
public class BuildingManagerTest {

    private static Engine testEngine;
    private static ExtendViewport gameWorldView;
    private static MapLoader mapLoaderSystem;
    private static AssetManager assetManager;

    // Tile size constant
    private static final int TILE_SIZE = 16;

    /**
     * Converts a tile X coordinate to a screen coordinate.
     *
     * @param tileCoord the tile coordinate.
     * @return the corresponding screen X coordinate.
     */
    private static float tileToScreenX(float tileCoord) {
        return tileCoord * TILE_SIZE;
    }

    /**
     * Converts a tile Y coordinate to a screen coordinate.
     * <p>
     * This method corrects for the inverted Y axis caused by the difference
     * between Tiled's top-left origin and libGDX's bottom-left origin.
     * </p>
     *
     * @param tileCoord the tile Y coordinate.
     * @return the corresponding screen Y coordinate.
     */
    private static float tileToScreenY(float tileCoord) {
        return -tileCoord * TILE_SIZE;
    }

    /**
     * Sets up the test environment.
     * <p>
     *     The test environment is set up by loading the test map and initializing the building manager system.
     *     The test asserts that the map is loaded and the bottom layer is initialized.
     *     The test also checks if screen conversion works.
     *     The test should pass.
     * </p>
     */
    @BeforeAll
    public static void setUp() {
        testEngine = new Engine();
        // Create an OrthographicCamera with world dimensions in pixels.
        OrthographicCamera camera = new OrthographicCamera(9 * TILE_SIZE, 9 * TILE_SIZE);
        // Set camera to y-up.
        camera.setToOrtho(false);
        // Create the viewport with the camera.
        gameWorldView = new ExtendViewport(9 * TILE_SIZE, 9 * TILE_SIZE, camera);
        // Force the screen bounds to start at (0,0) so that unproject interprets (0,0) as bottom left.
        gameWorldView.setScreenBounds(0, 0, 9 * TILE_SIZE, 9 * TILE_SIZE);
        // Update the viewport with screen dimensions (144 x 144) and center the camera.
        gameWorldView.update(9 * TILE_SIZE, 9 * TILE_SIZE, true);
        camera.update();

        try {
            String mapPath = "./testAssets/TMX/frontier_map_for_tests.tmx";
            FileHandle fileHandle = Gdx.files.internal(mapPath);
            assertTrue(fileHandle.exists(), "Map file should exist");

            mapLoaderSystem = new MapLoader();
            assetManager = new AssetManager();

            mapLoaderSystem.loadMap(assetManager, Path.of(mapPath));
            assetManager.finishLoading();
            mapLoaderSystem.initMapLayerEntities(testEngine);
            assertNotNull(mapLoaderSystem.getMap(), "Map should be loaded");
            assertNotNull(
                mapLoaderSystem.getMapEntity().getComponent(BottomLayerComponent.class),
                "Bottom layer should be loaded"
            );

            BuildingManagerSystem buildingManagerSystem = new BuildingManagerSystem(
                (TiledMapTileLayer) mapLoaderSystem.getMap().getLayers().get(0),
                gameWorldView,
                testEngine
            );
            testEngine.addSystem(buildingManagerSystem);
        } catch (Exception e) {
            fail("Failed to load map or initialize systems: " + e.getMessage());
        }

        checkIfScreenConversionWorks();
    }

    /**
     * Tests building placement on a buildable tile.
     * <p>
     *     The test creates a tower entity and sets its position to the screen coordinates
     *     corresponding to a buildable tile (5, 5). The building manager system is then used
     *     to place the building on the map. The test asserts that the building is placed successfully.
     *     The test should pass.
     * </p>
     */
    @Test
    public void testBuildingPlacementOnBuildableTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (5, 5)
        bp.position.x = tileToScreenX(5);
        bp.position.y = tileToScreenY(5);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile.");
    }

    /**
     * Tests building placement on a non-buildable tile.
     * <p>
     *     The test creates a tower entity and sets its position to the screen coordinates
     *     corresponding to a non-buildable tile (2, 2). The building manager system is then used
     *     to place the building on the map. The test asserts that the building is not placed.
     *     The test should pass.
     * </p>
     */
    @Test
    public void testBuildingNotPlacedOnNonBuildableTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (2, 2) which is non-buildable
        bp.position.x = tileToScreenX(2);
        bp.position.y = tileToScreenY(2);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(
            bms.placeBuilding(tower),
            "Building should not be placed on non-buildable tile"
        );
    }

    /**
     * Tests building placement on a resource tile.
     * <p>
     *     The test creates a tower entity and sets its position to the screen coordinates
     *     corresponding to a resource tile (0, 4). The building manager system is then used
     *     to place the building on the map. The test asserts that the building is not placed.
     *     The test should pass.
     * </p>
     */
    @Test
    public void testBuildingNotPlacedOnResourceTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (0, 4) which is a resource tile
        bp.position.x = tileToScreenX(0);
        bp.position.y = tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertFalse(bms.placeBuilding(tower), "Building should not be placed on resource tile");
    }

    /**
     * Tests building placement on an occupied tile.
     * <p>
     *     The test creates a tower entity and sets its position to the screen coordinates
     *     corresponding to a buildable tile (4, 4). The building manager system is then used
     *     to place the building on the map. The test asserts that the building is placed successfully.
     *     A second tower entity is then created with the same screen coordinates. The building manager
     *     system is used to place the second building on the map. The test asserts that the second building
     *     is not placed.
     *     The test should pass.
     * </p>
     */
    @Test
    public void testBuildingNotPlacedOnOccupiedTile() {
        Entity tower = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp = tower.getComponent(PositionComponent.class);
        // Set screen coordinates corresponding to tile (4, 4)
        bp.position.x = tileToScreenX(4);
        bp.position.y = tileToScreenY(4);

        BuildingManagerSystem bms = testEngine.getSystem(BuildingManagerSystem.class);
        assertTrue(bms.placeBuilding(tower), "Building should be placed on buildable tile");

        Entity tower2 = TowerFactory.createDefaultTower(testEngine);
        PositionComponent bp2 = tower2.getComponent(PositionComponent.class);
        // Set the same screen coordinates for the second tower
        bp2.position.x = tileToScreenX(4);
        bp2.position.y = tileToScreenY(4);

        assertFalse(bms.placeBuilding(tower2), "Building should not be placed on occupied tile");
    }

    /**
     * Removes all entities from the test engine.
     */
    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
    }

    // Helper method to check if screen conversion works
    private static void checkIfScreenConversionWorks() {
        // Validate conversion: a screen coordinate corresponding to tile (5,5) should yield world coordinate (5,5).
        int screenX = (int) tileToScreenX(5);
        int screenY = (int) tileToScreenY(5);
        // With the viewport's screen bounds set to (0,0,144,144), unprojecting should yield correct world coordinates.
        float worldX = BuildingUtils.calculateWorldCoordinate(
            gameWorldView,
            ((BottomLayerComponent) mapLoaderSystem
                    .getMapEntity()
                    .getComponent(BottomLayerComponent.class)).bottomLayer,
            screenX,
            screenY
        )
            .x;
        float worldY = BuildingUtils.calculateWorldCoordinate(
            gameWorldView,
            ((BottomLayerComponent) mapLoaderSystem
                    .getMapEntity()
                    .getComponent(BottomLayerComponent.class)).bottomLayer,
            screenX,
            screenY
        )
            .y;

        // Dividing by TILE_SIZE (16) should yield tile coordinate 5.
        assertEquals(5, worldX, "World coordinate x should be 5");
        assertEquals(5, worldY, "World coordinate y should be 5");
    }
}
