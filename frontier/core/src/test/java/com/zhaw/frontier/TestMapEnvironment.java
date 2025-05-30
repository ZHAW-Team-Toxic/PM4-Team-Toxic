package com.zhaw.frontier;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.zhaw.frontier.components.map.BottomLayerComponent;
import com.zhaw.frontier.components.map.DecorationLayerComponent;
import com.zhaw.frontier.components.map.ResourceLayerComponent;
import com.zhaw.frontier.exceptions.MapLoadingException;
import com.zhaw.frontier.systems.MapLoader;
import com.zhaw.frontier.utils.WorldCoordinateUtils;
import java.nio.file.Path;
import lombok.Getter;

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
 * - Properties on Tile: isBuildable: {false}, isSpawnPoint: {true},
 * isTraversable: {true}
 * Buildable area: 1
 * - Properties on Tile: isBuildable: {true}, isSpawnPoint: {false},
 * isTraversable: {true}
 * Water area: 2
 * - Properties on Tile: isBuildable: {false}, isTraversable: {false}
 * Wood resource tile: 3
 * - Properties on Tile: isBuildable: {false}, isTraversable: {true},
 * resourceType: {wood}
 * Stone resource tile: 4
 * - Properties on Tile: isBuildable: {false}, isTraversable: {true},
 * resourceType: {stone}
 * Iron resource tile: 5
 * - Properties on Tile: isBuildable: {false}, isTraversable: {true},
 * resourceType: {iron}
 *
 * Important:
 * - The map is 9x9 tiles.
 * - Tiles are 16 x 16 pixels.
 * - (0,0) is the bottom left corner of the map.
 * - Map gets rendered from bottom left corner.
 */
public class TestMapEnvironment {

    public static final int TILE_SIZE = 16;
    public static final int MAP_WIDTH_TILES = 9;
    public static final int MAP_HEIGHT_TILES = 9;

    @Getter
    private final Engine testEngine;

    @Getter
    private final ExtendViewport gameWorldView;

    @Getter
    private final TiledMap map;

    @Getter
    private final Entity mapEntity;

    private final AssetManager assetManager;

    /**
     * Constructs and initializes the test environment:
     * <ul>
     * <li>Sets up camera and viewport</li>
     * <li>Loads the test map and layer entities</li>
     * <li>Asserts coordinate conversion via
     * {@link #checkIfScreenConversionWorks()}</li>
     * </ul>
     *
     * @throws RuntimeException if the map file is missing or invalid
     */
    public TestMapEnvironment() {
        testEngine = new Engine();

        // Set up camera and viewport using the defined tile dimensions.
        OrthographicCamera camera = new OrthographicCamera(
            MAP_WIDTH_TILES * TILE_SIZE,
            MAP_HEIGHT_TILES * TILE_SIZE
        );
        camera.setToOrtho(false);
        gameWorldView =
        new ExtendViewport(MAP_WIDTH_TILES * TILE_SIZE, MAP_HEIGHT_TILES * TILE_SIZE, camera);
        gameWorldView.setScreenBounds(
            0,
            0,
            MAP_WIDTH_TILES * TILE_SIZE,
            MAP_HEIGHT_TILES * TILE_SIZE
        );
        gameWorldView.update(MAP_WIDTH_TILES * TILE_SIZE, MAP_HEIGHT_TILES * TILE_SIZE, true);
        camera.update();

        // Load the map using MapLoader.
        String mapPath = "testAssets/TMX/frontier_map_for_tests.tmx";
        FileHandle fileHandle = Gdx.files.internal(mapPath);
        if (!fileHandle.exists()) {
            throw new RuntimeException("Map file does not exist: " + mapPath);
        }
        MapLoader mapLoader = MapLoader.getInstance();
        assetManager = new AssetManager();
        try {
            mapLoader.loadMap(assetManager, Path.of(mapPath));
            assetManager.finishLoading();
            mapLoader.initMapLayerEntities(testEngine);
            map = mapLoader.getMap();
            mapEntity = mapLoader.getMapEntity();
            checkIfScreenConversionWorks();
        } catch (MapLoadingException e) {
            throw new RuntimeException("Failed to load map: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves the bottom layer of the map.
     *
     * @return the bottom {@link TiledMapTileLayer}
     * @throws RuntimeException if the BottomLayerComponent is missing
     */
    public TiledMapTileLayer getBottomLayer() {
        BottomLayerComponent bottomLayerComponent = mapEntity.getComponent(
            BottomLayerComponent.class
        );
        if (bottomLayerComponent == null) {
            throw new RuntimeException("BottomLayerComponent is missing from the map entity.");
        }
        return (TiledMapTileLayer) bottomLayerComponent.bottomLayer;
    }

    /**
     * Retrieves the decoration layer of the map.
     *
     * @return the decoration {@link TiledMapTileLayer}
     * @throws RuntimeException if the DecorationLayerComponent is missing
     */
    public TiledMapTileLayer getDecorationLayer() {
        DecorationLayerComponent decorationLayerComponent = mapEntity.getComponent(
            DecorationLayerComponent.class
        );
        if (decorationLayerComponent == null) {
            throw new RuntimeException("DecorationLayerComponent is missing from the map entity.");
        }
        return (TiledMapTileLayer) decorationLayerComponent.decorationLayer;
    }

    /**
     * Retrieves the resource layer of the map.
     *
     * @return the resource {@link TiledMapTileLayer}
     * @throws RuntimeException if the ResourceLayerComponent is missing
     */
    public TiledMapTileLayer getResourceLayer() {
        ResourceLayerComponent resourceLayerComponent = mapEntity.getComponent(
            ResourceLayerComponent.class
        );
        if (resourceLayerComponent == null) {
            throw new RuntimeException("ResourceLayerComponent is missing from the map entity.");
        }
        return (TiledMapTileLayer) resourceLayerComponent.resourceLayer;
    }

    /**
     * Disposes of the asset manager to free up resources.
     */
    public void dispose() {
        assetManager.dispose();
    }

    /**
     * Converts a tile X coordinate to a screen coordinate.
     *
     * @param tileCoord the tile coordinate.
     * @return the corresponding screen X coordinate.
     */
    public static float tileToScreenX(float tileCoord) {
        return tileCoord * TestMapEnvironment.TILE_SIZE;
    }

    /**
     * Converts a tile Y coordinate to a screen coordinate.
     * <p>
     * This method corrects for the inverted Y axis caused by the difference between
     * Tiled's top-left
     * origin and libGDX's bottom-left origin.
     * </p>
     *
     * @param tileCoord the tile Y coordinate.
     * @return the corresponding screen Y coordinate.
     */
    public static float tileToScreenY(float tileCoord) {
        return -tileCoord * TestMapEnvironment.TILE_SIZE;
    }

    /**
     * Validates that tile-to-screen coordinate conversion works as expected.
     *
     * <p>
     * This method converts tile (5,5) to screen coordinates and unprojects them
     * using {@link WorldCoordinateUtils#calculateWorldCoordinate}.
     * It asserts that the unprojected result matches (5,5) world coordinates.
     * </p>
     */
    private void checkIfScreenConversionWorks() {
        float screenX = TestMapEnvironment.tileToScreenX(5);
        float screenY = TestMapEnvironment.tileToScreenY(5);

        float worldX = WorldCoordinateUtils.calculateWorldCoordinate(
            gameWorldView,
            mapEntity.getComponent(BottomLayerComponent.class).bottomLayer,
            screenX,
            screenY
        )
            .x;

        float worldY = WorldCoordinateUtils.calculateWorldCoordinate(
            gameWorldView,
            mapEntity.getComponent(BottomLayerComponent.class).bottomLayer,
            screenX,
            screenY
        )
            .y;

        assertEquals(5, worldX, "World coordinate x should be 5");
        assertEquals(5, worldY, "World coordinate y should be 5");
    }
}
