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
import com.zhaw.frontier.components.ResourceCollectionRangeComponent;
import com.zhaw.frontier.components.ResourceProductionComponent;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.TiledPropertiesEnum;
import com.zhaw.frontier.entityFactories.ResourceBuildingFactory;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.systems.ResourceBuildingRangeSystem;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test class for the BuildingManagerSystem.
 * Tests the placement of buildings on the map.
 * This test uses the test map:
 * "TMX/frontier_map_for_tests.tmx"
 * It is structured like this:
 * <p>
 * Bottom layer and Resource layer combined:
 * <p>
 * 0,0,1,3,3,3,1,0,0,
 * 0,0,1,1,3,1,1,0,0,
 * 1,1,2,1,1,1,2,1,1,
 * 4,1,1,1,1,1,1,1,5,
 * 4,4,1,1,1,1,1,5,5,
 * 4,1,1,1,1,1,1,1,5,
 * 1,1,2,1,1,1,2,1,1,
 * 0,0,1,1,3,1,1,0,0,
 * 0,0,1,3,3,3,1,0,0
 * <p>
 * Spawn area: 0
 * - Properties on Tile: isBuildable: {false}, isSpawnPoint: {true}, isTraversable: {true}
 * Buildable area: 1
 * - Properties on Tile: isBuildable: {true}, isSpawnPoint: {false}, isTraversable: {true}
 * Water area: 2
 * - Properties on Tile: isBuildable: {false}, isTraversable: {false}
 * Wood resource tile: 3
 * - Properties on Tile: isBuildable: {false}, isTraversable: {true}, resourceType: {wood}
 * Stone resource tile: 4
 * - Properties on Tile: isBuildable: {false}, isTraversable: {true}, resourceType: {stone}
 * Iron resource tile: 5
 * - Properties on Tile: isBuildable: {false}, isTraversable: {true}, resourceType: {iron}
 * <p>
 * Important:
 * - The map is 9x9 tiles.
 * - Tiles are 16 x 16 pixels.
 * - (0,0) is the bottom left corner of the map.
 * - Map gets rendered from bottom left corner.
 */
@ExtendWith(GdxExtension.class)
public class ResourceBuildingRangeSystemTest {

    /** The Ashley engine used for testing. */
    private static Engine testEngine;

    /** The viewport representing the game world. */
    private static ExtendViewport gameWorldView;

    /** The map loader system responsible for loading the test map. */
    private static MapLoader mapLoaderSystem;

    /** The asset manager used to load map assets. */
    private static AssetManager assetManager;

    /** Tile size constant in pixels. */
    private static final int TILE_SIZE = 16;

    /**
     * Sets up the test environment.
     * <p>
     * The test environment is set up by loading the test map and initializing the building manager system.
     * The test asserts that the map is loaded and the bottom layer is initialized.
     * The test also checks if screen conversion works.
     * The test should pass.
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
            // Assert that the map file exists.
            assertTrue(fileHandle.exists(), "Map file should exist");

            mapLoaderSystem = new MapLoader();
            assetManager = new AssetManager();

            mapLoaderSystem.loadMap(assetManager, Path.of(mapPath));
            assetManager.finishLoading();
            mapLoaderSystem.initMapLayerEntities(testEngine);
            // Assert that the map and bottom layer are loaded.
            assertNotNull(mapLoaderSystem.getMap(), "Map should be loaded");
            assertNotNull(
                mapLoaderSystem.getMapEntity().getComponent(BottomLayerComponent.class),
                "Bottom layer should be loaded"
            );

            // Create and add the ResourceBuildingRangeSystem.
            ResourceBuildingRangeSystem resourceBuildingRangeSystem =
                new ResourceBuildingRangeSystem(
                    testEngine,
                    (TiledMapTileLayer) mapLoaderSystem
                        .getMap()
                        .getLayers()
                        .get(TiledPropertiesEnum.RESOURCE_LAYER.toString())
                );

            testEngine.addSystem(resourceBuildingRangeSystem);
        } catch (Exception e) {
            fail("Failed to load map or initialize systems: " + e.getMessage());
        }
    }

    /**
     * Tests that the resource collection range for a wood resource building with a range of 1 is correctly calculated.
     * <p>
     * A wood resource building is created with a range of 1, positioned at tile (4,6), and assigned a production rate of 1.
     * After updating the system, it is asserted that the number of resource tiles in range is 1.
     * </p>
     */
    @Test
    public void testRangeResourceCollectionCaseWood1() {
        ResourceBuildingRangeSystem resourceBuildingRangeSystem = testEngine.getSystem(
            ResourceBuildingRangeSystem.class
        );
        Entity woodBuilding = ResourceBuildingFactory.woodResourceBuilding(testEngine);
        woodBuilding.getComponent(ResourceCollectionRangeComponent.class).range = 1;
        woodBuilding.getComponent(PositionComponent.class).position.set(4, 6);
        woodBuilding
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(TiledPropertiesEnum.RESOURCE_TYPE_WOOD, 1);

        testEngine.addEntity(woodBuilding);

        resourceBuildingRangeSystem.update(0.1f);

        // Assert that the calculated number of resource tiles in range equals 1.
        assertEquals(
            1,
            woodBuilding.getComponent(ResourceCollectionRangeComponent.class).tilesInRange
        );
    }

    /**
     * Tests that the resource collection range for a wood resource building with a range of 2 is correctly calculated.
     * <p>
     * A wood resource building is created with a range of 2, positioned at tile (3,7), and assigned a production rate of 1.
     * After updating the system, it is asserted that the number of resource tiles in range is 4.
     * </p>
     */
    @Test
    public void testRangeResourceCollectionCaseWood2() {
        ResourceBuildingRangeSystem resourceBuildingRangeSystem = testEngine.getSystem(
            ResourceBuildingRangeSystem.class
        );
        Entity woodBuilding = ResourceBuildingFactory.woodResourceBuilding(testEngine);
        woodBuilding.getComponent(ResourceCollectionRangeComponent.class).range = 2;
        woodBuilding.getComponent(PositionComponent.class).position.set(3, 7);
        woodBuilding
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(TiledPropertiesEnum.RESOURCE_TYPE_WOOD, 1);

        testEngine.addEntity(woodBuilding);

        resourceBuildingRangeSystem.update(0.1f);

        // Assert that the calculated number of resource tiles in range equals 4.
        assertEquals(
            4,
            woodBuilding.getComponent(ResourceCollectionRangeComponent.class).tilesInRange
        );
    }

    /**
     * Tests that the resource collection range for a wood resource building with a range of 4 is correctly calculated.
     * <p>
     * A wood resource building is created with a range of 4, positioned at tile (4,4), and assigned a production rate of 1.
     * After updating the system, it is asserted that the number of resource tiles in range is 8.
     * </p>
     */
    @Test
    public void testRangeResourceCollectionCaseWood3() {
        ResourceBuildingRangeSystem resourceBuildingRangeSystem = testEngine.getSystem(
            ResourceBuildingRangeSystem.class
        );
        Entity woodBuilding = ResourceBuildingFactory.woodResourceBuilding(testEngine);
        woodBuilding.getComponent(ResourceCollectionRangeComponent.class).range = 4;
        woodBuilding.getComponent(PositionComponent.class).position.set(4, 4);
        woodBuilding
            .getComponent(ResourceProductionComponent.class)
            .productionRate.put(TiledPropertiesEnum.RESOURCE_TYPE_WOOD, 1);

        testEngine.addEntity(woodBuilding);

        resourceBuildingRangeSystem.update(0.1f);

        // Assert that the calculated number of resource tiles in range equals 8.
        assertEquals(
            8,
            woodBuilding.getComponent(ResourceCollectionRangeComponent.class).tilesInRange
        );
    }

    /**
     * Tears down the test environment by removing all entities and systems from the test engine,
     * and disposing of the asset manager.
     */
    @AfterAll
    public static void tearDown() {
        testEngine.removeAllEntities();
        testEngine.removeAllSystems();
        testEngine = null;
        mapLoaderSystem = null;
        assetManager.dispose();
        assetManager = null;
    }
}
