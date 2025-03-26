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
 * Helper class to initialize and manage the test map environment.
 * <p>
 * This class sets up the engine, viewport, and loads the test map ("TMX/frontier_map_for_tests.tmx"),
 * including its layers. It provides easy access to various map layers and environment properties for tests.
 * </p>
 */
public class TestMapEnvironment {

    public static final int TILE_SIZE = 16;
    public static final int MAP_WIDTH_TILES = 9;
    public static final int MAP_HEIGHT_TILES = 9;

    @Getter
    private final Engine testEngine;

    @Getter
    private final ExtendViewport viewport;

    @Getter
    private final TiledMap map;

    @Getter
    private final Entity mapEntity;

    private final AssetManager assetManager;

    /**
     * Initializes the test map environment by setting up the engine, viewport,
     * loading the map, and initializing its layers.
     *
     * @throws RuntimeException if the map file cannot be found or fails to load
     */
    public TestMapEnvironment() {
        testEngine = new Engine();

        // Set up camera and viewport using the defined tile dimensions.
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

        // Load the map using MapLoader.
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
}
