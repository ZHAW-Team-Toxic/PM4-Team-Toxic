package com.zhaw.tests;

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
 */
public class TestMapEnvironment {

    private final AssetManager assetManager;

    /** Tile size constant (in pixels) */
    @Getter
    private static final int TILE_SIZE = 16;

    /** Map width in tiles */
    @Getter
    private static final int MAP_WIDTH_TILES = 9;

    /** Map height in tiles */
    @Getter
    private static final int MAP_HEIGHT_TILES = 9;

    @Getter
    private final Engine testEngine;

    @Getter
    private final ExtendViewport viewport;

    @Getter
    private final TiledMap map;

    @Getter
    private final Entity mapEntity;

    /**
     * Initializes the test map environment by setting up the engine, viewport,
     * loading the map, and initializing its layers.
     *
     * @throws RuntimeException if the map file cannot be found
     */
    public TestMapEnvironment() {
        testEngine = new Engine();

        // Set up camera and viewport
        OrthographicCamera camera = new OrthographicCamera(
            MAP_WIDTH_TILES * TILE_SIZE,
            MAP_HEIGHT_TILES * TILE_SIZE
        );
        camera.setToOrtho(false);
        viewport =
        new ExtendViewport(MAP_WIDTH_TILES * TILE_SIZE, MAP_HEIGHT_TILES * TILE_SIZE, camera);
        viewport.setScreenBounds(0, 0, MAP_WIDTH_TILES * TILE_SIZE, MAP_HEIGHT_TILES * TILE_SIZE);
        viewport.update(MAP_WIDTH_TILES * TILE_SIZE, MAP_HEIGHT_TILES * TILE_SIZE, true);
        camera.update();

        // Load the map using MapLoader
        String mapPath = "./testAssets/TMX/frontier_map_for_tests.tmx";
        FileHandle fileHandle = Gdx.files.internal(mapPath);
        if (!fileHandle.exists()) {
            throw new RuntimeException("Map file does not exist: " + mapPath);
        }
        MapLoader mapLoader = new MapLoader();
        assetManager = new AssetManager();
        try {
            mapLoader.loadMap(assetManager, Path.of(mapPath));
            assetManager.finishLoading();
            mapLoader.initMapLayerEntities(testEngine);
            map = mapLoader.getMap();
            mapEntity = mapLoader.getMapEntity();
        } catch (MapLoadingException e) {
            throw new RuntimeException("Failed to load map: " + e.getMessage());
        }
    }

    /**
     * Gets the sample layer of the map.
     * @return the sample layer
     */
    public TiledMapTileLayer getSampleLayer() {
        return (TiledMapTileLayer) mapEntity.getComponent(BottomLayerComponent.class).bottomLayer;
    }

    /**
     * Gets the bottom layer of the map.
     * @return
     */
    public TiledMapTileLayer getBottomLayer() {
        return (TiledMapTileLayer) mapEntity.getComponent(BottomLayerComponent.class).bottomLayer;
    }

    /**
     * Gets the decoration layer of the map.
     * @return the decoration layer
     */
    public TiledMapTileLayer getDecorationLayer() {
        return (TiledMapTileLayer) mapEntity.getComponent(DecorationLayerComponent.class)
            .decorationLayer;
    }

    /**
     * Gets the resource layer of the map.
     * @return the resource layer
     */
    public TiledMapTileLayer getResourceLayer() {
        return (TiledMapTileLayer) mapEntity.getComponent(ResourceLayerComponent.class)
            .resourceLayer;
    }

    /**
     * Disposes of the asset manager.
     */
    public void dispose() {
        assetManager.dispose();
    }
}
